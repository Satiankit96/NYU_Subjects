#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <iomanip>
#include <map>
#include <cstdlib>
#include <stdio.h>
#include <string.h>


using namespace std;


//Constants Used accross file
const int MAX_MACHINE_SIZE = 512;
const int MAX_CHAR_SIZE  = 16;
const int MAX_LINE_COUNT = 16;
string word;
int tokenCnt=0;

//Global Variables to be used by multiple readings and classes
string filename;
ifstream file;


//extra method to replace to_string
string integerToString(int x) {
    ostringstream convert;
    convert << x;
    return convert.str();
}
 
class Error {
    int address;
    int instructionNumber;
    int moduleNumber;
    int code;
    string symbol;

public:
    void setAddress(int address) {
        this->address = address;
    }
    
    void setCode(int code) {
        this->code = code;
    }

    void setInstructionNumber(int instructionNumber) {
        this->instructionNumber = instructionNumber;
    }

    void setModuleNumber(int moduleNumber) {
        this->moduleNumber = moduleNumber;
    }

    void setSymbol(string symbol) {
        this->symbol = symbol;
    }

    int getAddress() {
        return address;
    }

    int getInstructionNumber() {
        return instructionNumber;
    }

    int getModuleNumber() {
        return moduleNumber;
    }

    string getErrorMessage() {
        //find message according to code
        switch(code) {
            case 8 : return "Error: Absolute address exceeds machine size; zero used";break;
            case 9 : return "Error: Relative address exceeds module size; zero used";break;
            case 6 : return "Error: External address exceeds length of uselist; treated as immediate";break;
            case 3 : return "Error: " + symbol + " is not defined; zero used";break;
            case 2 : return "Error: This variable is multiple times defined; first value used";break;
            case 10 : return "Error: Illegal immediate value; treated as 9999";break;
            case 11 : return "Error: Illegal opcode; treated as 9999";break;
            default: return "Error";
        }
    }
};


class Warning {
    int code;
    int moduleNumber;
    int size;
    string symbol;
    int moduleSize;

public :
    void setCode(int code) {
        this->code = code;
    }

    void setModuleNumber(int moduleNumber) {
        this->moduleNumber = moduleNumber;
    }

    void setSize(int size) {
        this->size = size;
    }
    
    void setSymbol(string symbol) {
        this->symbol = symbol;
    }

    void setModuleSize(int moduleSize) {
        this->moduleSize = moduleSize;
    }

    string getWarningMessage() {
        //send warning message according to code
        switch(code) {
            case 5 : return "Warning: Module " + integerToString(moduleNumber+1) + ": " + symbol + " too big " + integerToString(size) + " (max="+integerToString(moduleSize)+") assume zero relative";break;
            case 7 : return "Warning: Module " + integerToString(moduleNumber+1) + ": " + symbol + " appeared in the uselist but was not actually used";break;
            case 4 : return "Warning: Module " + integerToString(moduleNumber+1) + ": " + symbol + " was defined but never used";break;
            default : return "Error";
        }
    }
};


int errorLineNum=0;
int errorLineOffset=0;


void setLineNumAndOffset(bool isNextToken) {
    string line, token;
    file.open(filename.c_str());
    
    //handle case where the file ended, was unable to get next token altogether
    if(isNextToken) {
        
        while(getline(file, line)) {
            errorLineNum++;
            errorLineOffset = line.size();
        }
        errorLineOffset++;
        return;
    }

    while(getline(file, line)) {
        errorLineNum++;
        stringstream linestream(line);
        errorLineOffset = 0;
        while(getline(linestream, token,' ')) {
            if(!token.empty() && token != "\t" && token!=" ") {tokenCnt--;} 
            if(tokenCnt == 0) {
                errorLineOffset = line.find(token);
                errorLineOffset++;
                return;
            }
        }
    }
    file.close();
}

void __parseerror(int errcode, bool isNextToken) {
    file.close();
    setLineNumAndOffset(isNextToken);
    static char* errstr[] = {
        "NUM_EXPECTED", // Number expect, anything >= 2^30 is not a number either
        "SYM_EXPECTED", // Symbol Expected
        "ADDR_EXPECTED", // Addressing Expected which is A/E/I/R
        "SYM_TOO_LONG", // Symbol Name is too long
        "TOO_MANY_DEF_IN_MODULE", // > 16
        "TOO_MANY_USE_IN_MODULE", // > 16
        "TOO_MANY_INSTR" // total num_instr exceeds memory size (512)
    };
    printf("Parse Error line %d offset %d: %s\n", errorLineNum, errorLineOffset, errstr[errcode]);
}





//Base class for Symbol base
class Symbol {
    string name;
    int value;
    int absoluteAddress;
    int moduleNumber;
public :
    void setName(string name) {
        this->name = name;
    }

    void setValue(int value) {
        this->value = value;
    }

    void setAbsoluteAddress(int absoluteAddress) {
        this->absoluteAddress = absoluteAddress;
    }

    void setModuleNumber(int moduleNumber) {
        this->moduleNumber = moduleNumber;
    }

    string getName() const{
        return name;
    }

    int getValue() {
        return value;
    }

    int getAbsoluteAddress() {
        return absoluteAddress;
    }

    int getModuleNumber() {
        return moduleNumber;
    }
};


//Base class for Instruction Data in Instruction Set
class Instruction {
    char type;
    int address;
    int opcode;
    int operand;
    vector<Error> errorList;
public:
    void setType(char type) {
        this->type = type;
    }

    void setAddress(int address) {
        this->address = address;
    }

    void setOpcode(int opcode) {
        this->opcode = opcode;
    }

    void setOperand(int operand) {
        this->operand = operand;
    }

    void addToErrorList(Error e) {
        errorList.push_back(e);
    }

    int getAddress() {
        return address;
    }

    char getType() {
        return type;
    }

    vector<Error> getErrorList() {
        return errorList;
    }

};


//Class for containing moduke related data
class Module {
    int startAddress;
    int endAddress;
    vector<Symbol> definitionList;
    vector<string> useList;
    vector<Instruction> instructionList;
    vector<Error> errorList;
    vector<Warning> warningList;

public :
    void setStartAddress(int startAddress) {
        this->startAddress = startAddress;
    }

    void setEndAddress(int endAddress) {
        this->endAddress = endAddress;
    }

    void setDefinitonList(vector<Symbol> definitionList) {
        this->definitionList = definitionList;
    }

    void setUseList(vector<string> useList) {
        this->useList = useList;
    }

    void setInstructionList(vector<Instruction> instructionList) {
        this->instructionList = instructionList;
    }

    void addDefinition(Symbol definition) {
        definitionList.push_back(definition);
    }

    void addInstruction(Instruction instruction) {
        instructionList.push_back(instruction);
    }

    void addErrorList(Error e) {
        errorList.push_back(e);
    }

    void addWarningToList(Warning w) {
        warningList.push_back(w);
    }
    
    vector<Symbol> getDefinitionList() {
        return definitionList;
    }

    vector<string> getUseList() {
        return useList;
    }

    vector<Instruction> getInstructionList() {
        return instructionList;
    }

    int getInstructionSize() {
        return instructionList.size();
    }

    vector<Error> getErrorList() {
        return errorList;
    }

    vector<Warning> getWarningList() {
        return warningList;
    }
};




vector<pair<Symbol, int> > symbolTable;
vector<string> multipleSymbolsList;
vector<Module> moduleList;
vector<Symbol> defSymbolList;
vector<string> usedSymbol;
vector<Warning> globalWarningsList;


//function to update line number and offsets , useful in parse errors
void incrementTokenCnt() {
    tokenCnt++;
}


// functions for checks

bool isInteger(string token) {
    for(int i = 0; i < token.length(); i++) {
        if(!isdigit(token.at(i))) {
            return false;
        }
    }
    return true;
}

bool isCorrectDef(string token) {
    if(!isalpha(token.at(0))) {
        //current word is not correct, not next
        __parseerror(1, false);
        return false;
    }
    if(token.size() > MAX_CHAR_SIZE) {
        //current word is an issue, not next
        __parseerror(3, false);
        return false;
    }
    return true;
}

bool isInstructionType(string word) {
    if(word.size() > 1) {
        return false;
    }
    char k = word.at(0);
    if(k=='R' || k=='E' || k=='I' || k=='A') {
        return true;
    }
    return false;
}

bool isInVector(vector<string> checkVector, string value) {
    for(int i=0;i < checkVector.size();i++) {
        if(checkVector[i] == value) {
            return true;
        }
    }
    return false;
}


// contains all the functions for token conversion and readability


bool needNewLine = true;
int lineBuf[4096];

char *getToken() {
    char *tok;
    string currLine;
    while(needNewLine && !file.eof()) {
       getline(file, currLine);
       tok = strtok(&currLine[0], " \t\n");
       if(!tok) continue;
       needNewLine = false;
       return tok;
    }
    tok = strtok(NULL, " \t\n");
    if(!tok) {
        needNewLine = true;
        return getToken();
    }
    return tok;
}

vector<string> getTokenizedLine(string line) {
    vector<string> tokensList;
    char* ptr = &line[0];
    char* word;
    while ((word = strtok(ptr, " \t\n"))) {
        string k = word;
        tokensList.push_back(k);
        ptr = NULL;
    }
    return tokensList;
}

int readInt(string token) {
    return atoi(token.c_str());
}

void printLineNum(int currentCount) {
    if(currentCount < 10) {
        cout<<"00"<<currentCount;
    } else if(currentCount >= 10 && currentCount < 100) {
        cout<<"0"<<currentCount;
    } else {
        cout<<currentCount;
    }
    return;
}

void printInstructionAddress(int instructionAddress) {
    if(instructionAddress < 10) {
        cout<<"000"<<instructionAddress;
    } else if(instructionAddress >= 10 && instructionAddress < 100) {
        cout<<"00"<<instructionAddress;
    } else if(instructionAddress >= 100 && instructionAddress < 1000){
        cout<<"0"<<instructionAddress;
    } else {
        cout<<instructionAddress;
    }
    return;
}

Module createModule(int startAddress) {
    Module module;
    module.setStartAddress(startAddress);
    return module;
}

int calculateAbsAddress(int baseAddress, string relativeAddress) {
    return baseAddress + atoi(relativeAddress.c_str());
}

int findAddressInSymbolTable(string symbolName) {
    for(int i=0; i < symbolTable.size() ; i++) {
        if(symbolTable[i].first.getName() == symbolName) {
            return symbolTable[i].second;
        }
    }
    return -1;
}

bool inUsedSymbolList(string symbol) {
    for(int i=0;i<usedSymbol.size();i++) {
        if(usedSymbol[i] == symbol) {
            return true;
        }
    }
    return false;
}


bool presentInDefSymbolList(string symbolName) {
    for(int i=0; i< defSymbolList.size(); i++) {
        if(defSymbolList[i].getName() == symbolName) {
            return true;
        }
    }
    return false;
} 

void checkUseOfSymbolsForInstructionSet(vector<string> useList, vector<string> usedSymbol, int moduleIterator, Module &module) {
    for(int i = 0; i < useList.size(); i++) {
        if(!isInVector(usedSymbol, useList[i])) {
            Warning w;
            w.setCode(7);
            w.setModuleNumber(moduleIterator);
            w.setSymbol(useList[i]);
            module.addWarningToList(w);
        }
    }
}



void readDefinitionsForPass1(int defNums, int baseModuleAddress, int moduleIterator) {
    int tokenCount = 1;
    string prevToken;
    while(tokenCount <= defNums*2) {
        if(!(file>>word)) {
            if(tokenCount%2) {
                //case where there is no def name given
                __parseerror(1, true);
                exit(1);
            } else {
                //case where address of def name not given
                __parseerror(0, true);
                exit(1);
            }
        }
        string token = word;
        incrementTokenCnt();
        if(tokenCount%2) {
            if(!isCorrectDef(token)) {
                //case where def is not according to specifications
                //errors handled in correct def function
                exit(1);
            }

            if(findAddressInSymbolTable(token) != -1) {
                //case where symbol already present
                multipleSymbolsList.push_back(token);
                token = "multiple";
            }
            prevToken = token;
        } else {
            if(!isInteger(token)) {
                //case where the address is not integer
                __parseerror(0, false);
                exit(1);
            }
            //add to symbol table
            if(prevToken != "multiple") {
                Symbol symbol;
                symbol.setName(prevToken);
                symbol.setModuleNumber(moduleIterator);
                symbol.setAbsoluteAddress(calculateAbsAddress(baseModuleAddress, token));
                symbol.setValue(atoi(token.c_str()));
                symbolTable.push_back(pair<Symbol, int>(symbol, calculateAbsAddress(baseModuleAddress, token)));
            }
        }
        tokenCount++;  
    }
}


void readUsageListForPass1(int usageCount) {
    int currentUsageNum = 1;
    while(currentUsageNum <= usageCount) {
        if(!(file>>word)) {
            //case of usage list not equal to usage count
            // next word not found, hence true
            __parseerror(1, true);
            exit(1);
        }
        incrementTokenCnt();
        if(!isCorrectDef(word)) {
            //error parsing handled in function
            exit(1);
        }
        currentUsageNum++;
    }
}

void readInstructionListForPass1(int instructionCount) {
    int currentInstructionNum = 1, opcode, address;
    char instructionType;

    while(currentInstructionNum <= instructionCount*2) {
        if(!(file>>word)) {
            if(currentInstructionNum%2==1) {
                //expected , but wasn't hence next token
                __parseerror(2, true);
                exit(1);
            } else {
                //expected, but wasn't hence next token
                __parseerror(0, true);
                exit(1);
            }
        }
        incrementTokenCnt();
        //case of R,E,I,A
        if(currentInstructionNum%2==1) {
            if(!isInstructionType(word)) {
                //not the next word, this word failed
                __parseerror(2, false);
                exit(1);
            }
            instructionType = word.at(0);
        } 
        else {
            //case of instruction
            if(!isInteger(word)) {
                //this current instruction failed
                __parseerror(0, false);
                exit(1);
            }
            int instructionVal = atoi(word.c_str());
            opcode = instructionVal/1000; 
            address = instructionVal%1000;
            //checks on opcode and address in Pass 2 

        }
        currentInstructionNum++;
    }
}


//PASS 2 definitions , definitions usage and instructions


vector<Symbol> readDefinitionsForPass2(int definitionCount, int moduleNumber) {
    vector<Symbol> symbolList;
    string currSymbolName;
    int currToken = 1;
    while((currToken <= 2*definitionCount) && (file>>word)) {
        if(currToken%2==1) {
            currSymbolName = word;
            if(findAddressInSymbolTable(currSymbolName) == -1) {
                //if symbol not present in case of error
                currSymbolName = "ERROR";
            }
        } else {
            if(currSymbolName!="ERROR") {
                Symbol symbol;
                symbol.setName(currSymbolName);
                symbol.setAbsoluteAddress(findAddressInSymbolTable(currSymbolName));
                symbol.setModuleNumber(moduleNumber);
                symbolList.push_back(symbol);
                if(!presentInDefSymbolList(symbol.getName())) {
                    defSymbolList.push_back(symbol);
                } 
            }
        }
        currToken++;
    }
    return symbolList;
}


vector<string> readUsageListForPass2(int usageCount) {
    int currentUsageNum = 1;
    vector<string> usageList;
    string word;
    while((currentUsageNum <= usageCount) && (file>>word)) {
        usageList.push_back(word);
        currentUsageNum++;
    }
    return usageList;
}


vector<Instruction> readInstructionListForPass2(int instructionCount, int currentModAddress, vector<string>useList, int moduleIterator, Module &module) {
    vector<Instruction> instructionList;

    char type;
    string word;
    int opcode, address, currentInstructionCount = 1;

    while((currentInstructionCount <= 2*instructionCount) && (file>>word)) {
        if(currentInstructionCount%2==1) {
            //case of R,E,I,A
            type = word.at(0);
        } else {
            //case of instruction address
            Instruction in;
            in.setType(type);
            int defAddress = atoi(word.c_str());
            opcode = defAddress/1000;
            if(type == 'I') {
                if(defAddress > 9999) {
                    //error case of I going over 9999
                    Error e;
                    e.setModuleNumber(moduleIterator);
                    e.setCode(10);
                    module.addErrorList(e);
                    in.addToErrorList(e);
                    defAddress = 9999;
                }
                address = defAddress;
            } else if(opcode >= 10) {
                //illegal opcode first check
                Error e;
                e.setCode(11);
                e.setModuleNumber(moduleIterator);
                module.addErrorList(e);
                in.addToErrorList(e);
                address = 9999;
            } else if(type== 'R') {
                //case of R going over module size
                if(defAddress%1000 > instructionCount) {
                    Error e;
                    e.setCode(9);
                    module.addErrorList(e);
                    in.addToErrorList(e);
                    defAddress = opcode*1000 + 0;
                }
                address = defAddress + currentModAddress;
            } else if(type == 'A') {
                if(defAddress%1000 > MAX_MACHINE_SIZE) {
                    //error in case of A overshooting max size 
                    Error e;
                    e.setModuleNumber(moduleIterator);
                    e.setCode(8);
                    module.addErrorList(e);
                    in.addToErrorList(e);
                    defAddress = (defAddress/1000) * 1000;
                }
                address = defAddress;
            } 
            else {
                //case of 'E' or external, based on use list
                int index = defAddress%1000;
                if(index > useList.size()-1) {
                    Error e;
                    e.setCode(6);
                    module.addErrorList(e);
                    in.addToErrorList(e);
                    address = defAddress;
                } else {
                    int externalAddress = findAddressInSymbolTable(useList[index]);
                    if(externalAddress == -1) {
                        //unable to find symbol
                        Error e;
                        e.setInstructionNumber(currentInstructionCount);
                        e.setModuleNumber(moduleIterator);
                        e.setSymbol(useList[index]);
                        e.setCode(3);
                        module.addErrorList(e);
                        in.addToErrorList(e);
                        externalAddress = 0;                        
                    }
                    address = (defAddress/1000)*1000 + externalAddress; 
                    
                    //push symbols as used for later check
                    usedSymbol.push_back(useList[index]);
                }
            }
            in.setOpcode(defAddress/1000);
            in.setType(type);
            in.setAddress(address);
            instructionList.push_back(in);
        }
        currentInstructionCount++;
    }
    checkUseOfSymbolsForInstructionSet(useList, usedSymbol, moduleIterator, module);
    return instructionList;
}


// write base parsing logic for PASS 1

int readFilePass1() {
    file.open(filename.c_str());
    string token;
    string currLine;
    //start with module address as 0
    int currentModAddress = 0, moduleIterator = 0;
    if (file.is_open()) {
        while(file>>word) {

            // get definitions with (S,R) values
            incrementTokenCnt();
            if(!isInteger(word)) {
                //issue with current word not being integer
                __parseerror(0, false);
                exit(1);
            }
            int defNums = readInt(word);
            if(defNums > MAX_CHAR_SIZE) {
                //issue with current word going beyond size
                __parseerror(4, false);
                exit(1);
            }

            //check defintions and errors in Pass 1
            readDefinitionsForPass1(defNums, currentModAddress, moduleIterator);

            
            //get usages of S values
            if(!(file>>word)) {
                //unable to find S value
                __parseerror(0, true);
                exit(1);
            }
            incrementTokenCnt();
            if(!isInteger(word)) {
                //current tokem is mot a word
                __parseerror(0, false);
                exit(1);
            }
            int usageCount = readInt(word);
            if(usageCount > MAX_LINE_COUNT) {
                //too may uses parse error, should declare for same point
                __parseerror(5, false);
                exit(1);
            }

            //check the individual usage list and count
            readUsageListForPass1(usageCount); 


            //get instruction set for pass 1
            if(!(file>>word)) {
                //instructions not found, looking for next token
                __parseerror(0, true);
                exit(1);
            }
            incrementTokenCnt();
            if(!isInteger(word)) {
                //word not found in current
                __parseerror(0, false);
                exit(1);
            }
            int instructionCount = readInt(word);
            if(instructionCount + currentModAddress > MAX_MACHINE_SIZE) {
                //current instruction count an issue
                __parseerror(6, false);
                exit(1);
            }
            readInstructionListForPass1(instructionCount);

            currentModAddress += instructionCount;

            //add the warnings post pass 1 (rule 5)
            //length equivalent to instructionCount
            for(int i=0; i< symbolTable.size(); i++) {
                int relValue = symbolTable[i].first.getValue();
                if(symbolTable[i].first.getModuleNumber()==moduleIterator &&  relValue > instructionCount-1) {
                    //case where beyond size
                    Warning w;
                    w.setCode(5);
                    w.setSymbol(symbolTable[i].first.getName());
                    w.setModuleNumber(moduleIterator);
                    w.setSize(relValue);
                    w.setModuleSize(instructionCount-1);
                    globalWarningsList.push_back(w);
                    //replace with zero relative
                    symbolTable[i].first.setValue(0);
                    symbolTable[i].first.setAbsoluteAddress(currentModAddress-instructionCount);
                    symbolTable[i].second = currentModAddress-instructionCount;
                }
            }
            moduleIterator++;
        }
    }
    file.close();
    return 1;
}

//write base logic for parsing file for PASS 2

int readFilePass2() {
    file.open(filename.c_str());
    string currLine;
    //start with module address as 0
    int currentModAddress = 0, moduleIterator = 0;


    if(file.is_open()) {
        while(file>>word) {
            Module module;

            //read definitions
            
            int defintionCount = atoi(word.c_str());
            module.setDefinitonList(readDefinitionsForPass2(defintionCount, moduleIterator));

            //read and store usages
            file>>word;
            int usageCount = atoi(word.c_str());
            module.setUseList(readUsageListForPass2(usageCount));

            //read and relate addressess for instructions
            file>>word;
            int instuctionCount = atoi(word.c_str());
            module.setInstructionList(readInstructionListForPass2(instuctionCount, currentModAddress, module.getUseList(), moduleIterator, module));
            
            //add module to final list for bookkeeping
            moduleList.push_back(module);
            currentModAddress += module.getInstructionSize();
            moduleIterator++;
        }
    }


    //print Warnings list global rule 5 ?
    for(int i=0; i< globalWarningsList.size(); i++) {
        cout<<globalWarningsList[i].getWarningMessage()<<endl;
    }
    cout<<endl;


    //print the Symbol Table
    cout<<"Symbol Table" << endl;
    for(int i = 0; i < symbolTable.size(); i++) {
        cout<<symbolTable[i].first.getName()<<"="<<symbolTable[i].second;
        for(int j = 0; j < multipleSymbolsList.size(); j++) {
            if(multipleSymbolsList[j] == symbolTable[i].first.getName()) {
               //print message string for warning/error
                cout<<" Error: This variable is multiple times defined; first value used";
            }
        }
        cout<<endl;
    }
    cout<<endl;
    
    //now print the modules accordingly 
    cout<<"Memory Map"<<endl;
    int cnt = 0;
    for(int i = 0; i < moduleList.size(); i++) {
        Module currModule = moduleList[i];
        for(int j=0; j < currModule.getInstructionSize(); j++) {
            vector<Instruction> instructionList = currModule.getInstructionList();
            printLineNum(cnt);
            cout<<": ";
            printInstructionAddress(instructionList[j].getAddress());
            if(instructionList[j].getErrorList().size() > 0) {
                cout<<" " << instructionList[j].getErrorList()[0].getErrorMessage();
            }
            cout<<endl;
            cnt++;
        }
        //print end of module warnings rule 7
        for(int k=0; k < currModule.getWarningList().size(); k++) {
            cout << currModule.getWarningList()[k].getWarningMessage() << endl; 
        }
    }
    cout<<endl;

    //print end warnings rule 4
    for(int i=0; i<defSymbolList.size(); i++) {
        if(!inUsedSymbolList(defSymbolList[i].getName())) {
            Warning w;
            w.setCode(4);
            w.setSymbol(defSymbolList[i].getName());
            w.setModuleNumber(defSymbolList[i].getModuleNumber());
            cout << w.getWarningMessage() << endl;
        }
    }
    
    file.close();
    return 1;
}


int main(int argc, char* argv[]) {
    filename = argv[1];
    //filename = "example.txt";
    readFilePass1();
    readFilePass2();
    
    //clear and close out variables
    symbolTable.clear();
    multipleSymbolsList.clear();

    return 0;
}
