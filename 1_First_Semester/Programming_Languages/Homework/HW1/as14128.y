%{
#include<iostream>
#include<stdlib.h>
#include<math.h>
#include<string.h>
#include<stdio.h>
#include<map>
#include<iterator>
#include<algorithm>
#include "util.h"

int yylex();
int yyerror(const char *p) {std::cerr<<"ERROR: "<<p<<std::endl;exit(-1);};


std::map<std::string,double> keyToValueMapping;

const std::string WHITESPACE = " \n\r\t\f\v";
 
std::string ltrim(const std::string &s)
{
    size_t start = s.find_first_not_of(WHITESPACE);
    return (start == std::string::npos) ? "" : s.substr(start);
}
 
std::string rtrim(const std::string &s)
{
    size_t end = s.find_last_not_of(WHITESPACE);
    return (end == std::string::npos) ? "" : s.substr(0, end + 1);
}
 
std::string trim(const std::string &s) {
    return rtrim(ltrim(s));
}




bool isValueInMap(char* key) {
    std::map<std::string, double>::iterator it;
    std::string newKey(key);
    for(it = keyToValueMapping.begin(); it != keyToValueMapping.end(); ++it) {
        if(strcmp(it->first.c_str(), newKey.c_str()) == 0) {
            return true;
        }
    }
    return false;   
}

double findValueInMap(char* key) {
    std::map<std::string, double>::iterator it;
    std::string newKey(key);
    for(it = keyToValueMapping.begin(); it != keyToValueMapping.end(); ++it) {
        if(strcmp(it->first.c_str(), newKey.c_str()) == 0) {
            return it->second;
        }
    }
    return 0;
}


double insertValueInMap(char* key, double value) {
    std::string newKey(key);
    newKey = trim(newKey.substr(0, newKey.find("=")));
    keyToValueMapping.erase(newKey);
    keyToValueMapping.insert(std::pair<std::string, double>(newKey, value));
    return value;
}

%}


%union {
    double val;
    char* variableName;
}



%start program_input

%token<val> NUMBER
%token<val> LBRACKET RBRACKET
%token<val> DIV MUL ADD SUB EQUALS
%token<val> PI
%token<val> POW SQRT FACTORIAL MOD
%token<val> LOG2 LOG10
%token<val> FLOOR CEIL ABS
%token<val> GBP_TO_USD USD_TO_GBP 
%token<val> GBP_TO_EURO EURO_TO_GBP 
%token<val> USD_TO_EURO EURO_TO_USD
%token<val> COS SIN TAN COSH SINH TANH
%token<val> CEL_TO_FAH FAH_TO_CEL
%token<val> MI_TO_KM KM_TO_MI
%token<val> VAR_KEYWORD 
%token<val> EOL
%token<variableName> VARIABLE

%type<val> program_input
%type<val> line 
%type<val> calculation 
%type<val> constant
%type<val> expr 
%type<val> function 
%type<val> trig_function 
%type<val> log_function 
%type<val> conversion 
%type<val> temp_conversion 
%type<val> dist_conversion
%type<val> assignment


/* BODMAS rules to be set, left to right (?)*/
%left SUB
%left ADD
%left MUL DIV MOD
%left POW SQRT
%left LBRACKET RBRACKET



%%
program_input: 
             | program_input line                           {printf("=%0.2f\n", $2);}
             ;
line: EOL                                                   
    | calculation EOL                                       
    ;
calculation: expr                                          
           | assignment                                     
           ;
constant: PI                                                {$$ = 3.142;}
        ;
expr: SUB expr                                              {$$ = -$2;}
    | NUMBER                                                
    | VARIABLE                                              {if(!isValueInMap($1)){yyerror("Undefined symbol");} else {$$ = findValueInMap($1);}}
    | constant                                              
    | function                                              
    | expr DIV expr                                         {if($3 == 0){yyerror("Division by zero");} else {$$ = $1 / $3;}}
    | expr MUL expr                                         {$$ = $1 * $3;}
    | expr ADD expr                                         {$$ = $1 + $3;}
    | expr SUB expr                                         {$$ = $1 - $3;}
    | expr POW expr                                         {$$ = pow($1, $3);}
    | expr MOD expr                                         {$$ = modulo($1, $3);}
    | LBRACKET expr RBRACKET                                {$$ = $2;}
    ;
function: conversion                                         
        | log_function                                       
        | trig_function                                      
        | expr FACTORIAL                                    {$$ = factorial($1);}
        | SQRT expr                                         {$$ = sqrt($2);}
        | ABS expr                                          {$$ = abs($2);}
        | FLOOR expr                                        {$$ = floor($2);}
        | CEIL expr                                         {$$ = ceil($2);}
        ;
trig_function: COS expr                                     {$$ = cos($2);}
             | SIN expr                                     {$$ = sin($2);}
             | TAN expr                                     {$$ = tan($2);}
             ;
log_function: LOG2 expr                                     {$$ = log2($2);}
            | LOG10 expr                                    {$$ = log10($2);}
            ;
conversion: temp_conversion                                  
          | dist_conversion                                  
          | expr GBP_TO_USD                                 {$$ = gbp_to_usd($1);}
          | expr USD_TO_GBP                                 {$$ = usd_to_gbp($1);}
          | expr GBP_TO_EURO                                {$$ = gbp_to_euro($1);}
          | expr EURO_TO_GBP                                {$$ = euro_to_gbp($1);}
          | expr USD_TO_EURO                                {$$ = usd_to_euro($1);}
          | expr EURO_TO_USD                                {$$ = euro_to_usd($1);}
          ;
temp_conversion: expr CEL_TO_FAH                            {$$ = cel_to_fah($1);}
               | expr FAH_TO_CEL                            {$$ = fah_to_cel($1);}
               ;
dist_conversion: expr MI_TO_KM                              {$$ = m_to_km($1);}
               | expr KM_TO_MI                              {$$ = km_to_m($1);}
               ;
assignment: VAR_KEYWORD VARIABLE EQUALS calculation         {$$ = insertValueInMap($2, $4);}
%%


int main() {
    yyparse();
    return 0;
}
