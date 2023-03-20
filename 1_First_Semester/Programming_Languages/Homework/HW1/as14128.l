%{
#include<stdio.h>
#include<stdlib.h>
#include "y.tab.h"
%}

%option noyywrap

%%
[ \t\r]+                        {}
("PI")|("pi")                   {return PI;}
[0-9]+(\.[0-9]+)?               {yylval.val = atof(yytext);return NUMBER;}
"-"                             {return SUB;}
"+"                             {return ADD;}
"*"                             {return MUL;}
"/"                             {return DIV;}
"^"                             {return POW;}
"%"                             {return MOD;}
"("                             {return LBRACKET;}
")"                             {return RBRACKET;}
("SQRT")|("sqrt")               {return SQRT;}
"!"                             {return FACTORIAL;}
("ABS")|("abs")                 {return ABS;}
("FLOOR")|("floor")             {return FLOOR;}
("CEIL")|("ceil")               {return CEIL;}
("SIN")|("sin")                 {return SIN;}
("TAN")|("tan")                 {return TAN;}
("LOG2")|("log2")               {return LOG2;}
("LOG10")|("log10")             {return LOG10;}
("GBP_TO_USD")|("gbp_to_usd")   {return GBP_TO_USD;}
("USD_TO_GBP")|("usd_to_gbp")   {return USD_TO_GBP;}
("GBP_TO_EURO")|("gbp_to_euro") {return GBP_TO_EURO;}
("EURO_TO_GBP")|("euro_to_gbp") {return EURO_TO_GBP;}
("USD_TO_EURO")|("usd_to_euro") {return USD_TO_EURO;}
("EURO_TO_USD")|("euro_to_usd") {return EURO_TO_USD;}
("CEL_TO_FAH")|("cel_to_fah")   {return CEL_TO_FAH;}
("FAH_TO_CEL")|("fah_to_cel")   {return FAH_TO_CEL;}
("MI_TO_KM")|("mi_to_km")       {return MI_TO_KM;}
("KM_TO_MI")|("km_to_mi")       {return KM_TO_MI;}
("VAR")|("var")                 {return VAR_KEYWORD;}
("MOD")|("mod")                 {return MOD;}    
[a-zA-Z][a-zA-Z0-9]*            {yylval.variableName = yytext;return VARIABLE;}
"="                             {return EQUALS;}
[\n]                            {return EOL;}
%%