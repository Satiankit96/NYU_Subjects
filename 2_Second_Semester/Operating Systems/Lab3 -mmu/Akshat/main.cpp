#include <stdio.h>
#include <iostream>
#include <getopt.h>
#include <string.h>
#include <sstream>
#include <string>
#include<vector>
#include <deque>

using namespace std;

const int MAX_VPAGES = 64; 

struct frame {
    int pid = -1;
    int vpage = -1;

    bool is_mapped = false;
    int f_index;

    unsigned int age : 32 ;         // for aging
    unsigned int tlu = 0;           // for working set
};


struct stats {
    int segv = 0;
    int segprot = 0; 
    int unmap = 0;
    int map=0;
    int in = 0;
    int fin = 0;
    int out =0;
    int fout =0;
    int zero = 0;
};

vector<frame> frame_table; 
deque<int> free_frames;
int num_frames;


struct pte {
    unsigned int present : 1;
    unsigned int referenced : 1;
    unsigned int modified : 1;
    unsigned int writeProtect : 1;
    unsigned int pagedOut : 1;
    unsigned int frameIndex : 7;
    unsigned int isFileMapped : 1;
    unsigned int padding : 19;
};

void reset_pte(pte *paget){  

    paget->present = 0;
    paget->referenced = 0;
    paget->modified = 0;
    paget->writeProtect = 0;
    paget->frameIndex = 0;
    paget->isFileMapped = 0;
    paget->padding = 0; 
} 

struct VMA {
   
  int start_page;
  int end_page;
  int write_protected;
  int file_mapped;
};

class Process {
    public:
    int pid;
    vector<VMA> vmas;
    pte pageTable[MAX_VPAGES]; 
    stats stat;

    void create_pt(){
        for(int i=0; i< MAX_VPAGES; i++) {
            reset_pte(&pageTable[i]);
            pageTable[i].pagedOut = 0;
        }
    }
};

vector <Process> p;
vector <int> randvals;
int rfile_length;
int ofs = -1;
deque <pair<char, int>> instructions;
int oflag = 0;
int inst = -1;
int ctx_switches = 0;
int process_exits = 0;
int curr_process = -1;


void print_pt() {

    for (int i=0; i<p.size();i++) {
        cout << "PT[" << i << "]:";

        //print each page info
        for (int j=0;j<MAX_VPAGES;j++) {
           
           if (p[i].pageTable[j].present == 0) {
               if (p[i].pageTable[j].pagedOut == 1) cout << " #"; 
                else cout << " *";
           } else {
               cout << " " << j << ":";
           if (p[i].pageTable[j].referenced) cout << "R";
           else cout << "-";

           if (p[i].pageTable[j].modified) cout << "M";
           else cout << "-";
 
            if (p[i].pageTable[j].pagedOut) cout << "S";
           else cout << "-";
           }              
        }
        cout << endl;
    }
}

void print_ft() {

    cout << "FT:";
    for (int i=0; i < frame_table.size(); i++) {
        if (frame_table[i].is_mapped){
            cout << " " << frame_table[i].pid << ":" << frame_table[i].vpage ; 
        } 
        else cout << " *";
    }
    cout << endl;
}

void print_sum() {
    unsigned long long cost = 0;
    for (int i=0; i<p.size();i++) {

        printf("PROC[%d]: U=%lu M=%lu I=%lu O=%lu FI=%lu FO=%lu Z=%lu SV=%lu SP=%lu\n",
                        i,
                       p[i].stat.unmap, p[i].stat.map, p[i].stat.in, p[i].stat.out,
                        p[i].stat.fin, p[i].stat.fout, p[i].stat.zero,
                        p[i].stat.segv, p[i].stat.segprot);
        cost += (p[i].stat.unmap * 400) + (p[i].stat.map * 300) + (p[i].stat.in * 3100) + (p[i].stat.out * 2700) + (p[i].stat.fin * 2800) +
                    (p[i].stat.fout * 2400) + (p[i].stat.zero * 140) + (p[i].stat.segv * 340) + (p[i].stat.segprot*420)  ;
    }

    cost += (inst - ctx_switches - process_exits+ 1) + (ctx_switches* 130) + (process_exits* 1250);
    printf("TOTALCOST %lu %lu %lu %llu %lu\n",
                    inst+1, ctx_switches, process_exits, cost, (unsigned long)sizeof(pte));
}

int myrandom(int burst) { 
    
    ofs = (ofs + 1)% rfile_length;
    return randvals[ofs] % burst; 
    }

void read_rfile(char *fileloc){
    // read the rfile numbers into a vector randvals.
    
    FILE *r_file = fopen(fileloc, "r");
    char line[50];
    int lineno=0;
    while (fgets(line, sizeof(line), r_file) != NULL) {
        
        if (lineno == 0) { // to ignore the first line which has the count of random numbers 
            rfile_length = atoi(strtok(line, " \t\n"));
            lineno ++;
            }               
        
        else {randvals.push_back(atoi(strtok(line, " \t\n")));}
    }
    fclose(r_file);
}


class Pager {
    public:
    virtual frame* select_victim_frame() = 0;
    virtual void reset_age_bit(int f) {}
};

class FIFO : public Pager {
    public:
    int hand = -1;
    frame* select_victim_frame() {
        hand = (hand + 1) % num_frames;
        return &frame_table[hand];
    }
};

class ClockPolicy : public Pager {
    public:
    int hand = 0;
    frame* select_victim_frame() {
        bool flag = false;

        while(flag != true) {
            
            if (p[frame_table[hand].pid].pageTable[frame_table[hand].vpage].referenced == 0) {    
                flag = true;
                int temp = hand;
                hand = (hand + 1) % num_frames;
                return &frame_table[temp];
            }
            else {
                p[frame_table[hand].pid].pageTable[frame_table[hand].vpage].referenced = 0;
                hand = (hand + 1) % num_frames;
            } 
        }
        return &frame_table[hand];
    }
};

class randomPolicy : public Pager {
    public:
    frame* select_victim_frame() {
        return &frame_table[myrandom(num_frames)];
    }
};

int last_reset = 0;

class NRU : public Pager {
    public:
    int hand = 0;
    frame* select_victim_frame() {
        
        vector<int> Class = {-1,-1,-1,-1};
        int victim = -1;
        bool reset = false;

        if (last_reset >= 50){reset = true; last_reset = 0; }

        for(int i=0; i<num_frames;i++) {
            
            int r = p[frame_table[hand].pid].pageTable[frame_table[hand].vpage].referenced;
            int m = p[frame_table[hand].pid].pageTable[frame_table[hand].vpage].modified;
            int class_val = 2*r + m;
            
            //cout << "r & m: " << r << m << " hand : " << hand <<  endl;
            if (Class[class_val] == -1) {
                
                if ((class_val == 0) && !reset) {
                    victim = hand;
                    hand = (hand + 1) % num_frames;
                    return &frame_table[victim];
                }
                Class[class_val] = hand;
            }

            if (reset) {p[frame_table[hand].pid].pageTable[frame_table[hand].vpage].referenced = 0;}
            
            hand = (hand + 1) % num_frames;  
        }

        for(int i=0; i<4;i++) {
            if (Class[i] != -1) {
                victim  = Class[i];
                hand = (victim+1) % num_frames;
                break;
            }
        }
        return &frame_table[victim];
    }
};

class Aging : public Pager {
    public:
    int hand = 0;
    frame* select_victim_frame() {
        
        int min_age_index = hand;

        for (int i =0; i< num_frames;i++){
            
            frame_table[hand].age >>= 1;       // shift age bit to right
            if (p[frame_table[hand].pid].pageTable[frame_table[hand].vpage].referenced){
                frame_table[hand].age |= 0x80000000;       // insert refernce bit to leading bit
            }

            // maintaining frame index with lowest index
            if (frame_table[hand].age < frame_table[min_age_index].age) {
                min_age_index = hand;
            }
            
            p[frame_table[hand].pid].pageTable[frame_table[hand].vpage].referenced = 0;
            hand = (hand+1) % num_frames;
        }

        hand = (min_age_index + 1) % num_frames;
        return &frame_table[min_age_index];
    }

    void reset_age_bit(int f){
        frame_table[f].age = 0x00000000;
    }
};

class WorkingSet : public Pager {
    public:
    int hand = 0;

    frame* select_victim_frame() {
        
        int victim = hand;
        
        for (int i=0;i<num_frames;i++){

            unsigned int r = p[frame_table[hand].pid].pageTable[frame_table[hand].vpage].referenced;
            int age = inst - frame_table[hand].tlu;
            //cout << frame_table[hand].f_index << " : r= "<< r << " : "<< frame_table[hand].tlu << "\t" ; 

            if (r == 1){
                frame_table[hand].tlu = inst; 
                p[frame_table[hand].pid].pageTable[frame_table[hand].vpage].referenced = 0;
            }
            else if (r == 0 && age >= 50){
                victim = hand;
                break;
            }
            else if (r == 0 && age < 50) {
                if (frame_table[hand].tlu < frame_table[victim].tlu) {victim = hand;}
            }
            hand = (hand+1) % num_frames;
        }
        hand = (victim+1)% num_frames;
        return &frame_table[victim];
    }
};

Pager* pagermode;

void  getPager(char x){    
        if (x == 'f'){
            pagermode  = new FIFO();
        } else if (x == 'r') {
            pagermode = new randomPolicy();
        } else if (x == 'c') {
            pagermode  = new ClockPolicy();
        } else if (x == 'e') {
            pagermode  = new NRU();
        } else if (x == 'a') {
            pagermode  = new Aging();
        } else if (x == 'w') {
            pagermode  = new WorkingSet();
        } 
}


frame* allocated_from_free_list() {
    if (!free_frames.empty()) {
        int t = free_frames.front();
        free_frames.pop_front();
        return &frame_table[t];
    }
    else return NULL; 
}

frame* get_frame(){
    frame* f = allocated_from_free_list();
    if (f == NULL) f = pagermode->select_victim_frame();
    return f; 
}

char algo;
string options = "";

void read_input_file(char *fileloc){
    // read the given input file; generate a process object for each process and store into a vector p.
    
    FILE *input_file = fopen(fileloc, "r");

    char buf[512];
    int lineno=0;
    int num_process;
    int num_vma = 0;
    char op;
    int pg;
    

    string line;
    stringstream clean_file;
    
    // clean the input file
    while (fgets(buf, sizeof(buf), input_file) != NULL) {
        if (buf[0] == '#') continue; 
        clean_file << buf;
    }
    
    getline(clean_file, line);
    sscanf(line.c_str(), "%d", &num_process);

    for (int i =0; i<num_process; i++) {
        Process p1;
        p1.pid = i;

        getline(clean_file, line);
        sscanf(line.c_str(), "%d", &num_vma); 

        for (int j=0;j<num_vma;j++) {
            VMA v;

            getline(clean_file, line);
            sscanf(line.c_str(), "%d %d %d %d", &v.start_page, &v.end_page, &v.write_protected, &v.file_mapped);

            p1.vmas.push_back(v);
        }

        p1.create_pt();
        p.push_back(p1);
    }

    // storing the instructions
    while(getline(clean_file, line)) {
        sscanf(line.c_str(), "%s %d", &op, &pg);
        instructions.push_back(make_pair(op,pg));
    }
    fclose(input_file);   
}

void initialize_frames(){
    for (int i=0; i< num_frames;i++){
        frame_table[i].f_index = i;
        free_frames[i] = i;
        frame_table[i].age = 0x00000000;
    }
}

void check_inputs() {
    // PRINT THE INPUTS
    for (int i=0; i < p.size(); i++) {
                for (int j=0; j < p[i].vmas.size(); j++) {
                    cout << p[i].vmas[j].start_page << " \t" << p[i].vmas[j].end_page << endl; //<<  " " <<p[i].vmas[j]->end_page; 
                }     
             cout << endl;
            }
        
    for (int i=0; i<instructions.size();i++){    
                cout << instructions[i].first << "\t" << instructions[i].second << endl;
     }
}


bool check_vmas(int proc, int pg) {
    for (int i =0 ; i< p[proc].vmas.size();i++ ){

        if ((p[proc].vmas[i].start_page <= pg) && (pg <= p[proc].vmas[i].end_page)) {
                
            //set the filemapped and writeprotected bit accordingly
            p[proc].pageTable[pg].isFileMapped = p[proc].vmas[i].file_mapped; 
            p[proc].pageTable[pg].writeProtect =  p[proc].vmas[i].write_protected;
            return true;  
        } 
    }
    return false;
}

void simulation() {
    pair<char, int> g;

    while(!instructions.empty()){
        last_reset++;
        g = instructions.front();
        instructions.pop_front();

        inst++; 
        if (oflag) cout << inst  << ": ==> " <<  g.first << " " << g.second << endl;

        if (g.first == 'c') {           // handle c instr
            ctx_switches++;
            curr_process = g.second;
            continue;
        }

        
        if (g.first == 'e') {           // handle e instr
            process_exits++;
        
            cout << "EXIT current process " << curr_process << endl;
            
            for (int i =0; i< MAX_VPAGES;i++) {
                if (p[curr_process].pageTable[i].present) {

                    // unmap
                    if (oflag) cout << " UNMAP " << curr_process << ":" << i << endl;
                    p[curr_process].stat.unmap++; 
                    frame_table[p[curr_process].pageTable[i].frameIndex].is_mapped = false;

                    //fout
                    if (p[curr_process].pageTable[i].modified && p[curr_process].pageTable[i].isFileMapped) {
                        if (oflag) cout << " FOUT" << endl;
                        p[curr_process].stat.fout++;
                        
                    }

                    // return used frame
                    free_frames.push_back(p[curr_process].pageTable[i].frameIndex);


                }
                    p[curr_process].pageTable[i].pagedOut = 0;
                    reset_pte(&p[curr_process].pageTable[i]);
            }
            continue;
        }
        
        
        pte *pt = &p[curr_process].pageTable[g.second];

        if (pt->present==0) {
            
            // call pgfault handler
            
            // check if vpage part of any vma 
            if(!check_vmas(curr_process, g.second)){
               if (oflag) cout << " SEGV" << endl;
               p[curr_process].stat.segv++;
                continue;
            }
            else {
                // if it is part of a vma then instantiate it. 
                frame* newframe = get_frame();

                // check if newframe is mapped
                if(newframe->is_mapped){
                    // unmap 
                   if (oflag) cout << " UNMAP " << newframe->pid << ":" << newframe->vpage << endl;
                   p[newframe->pid].stat.unmap++; 
                    // check old vpage R and M bits. pagedout or filemapped if modified.
                    pte *old =  &p[newframe->pid].pageTable[newframe->vpage];
                    if (old->modified == 1) {

                        old->modified = 0;
                        old->pagedOut = 1;
                        
                        if (old->isFileMapped == 1) {old->pagedOut = 0; p[newframe->pid].stat.fout++; if (oflag) cout << " FOUT" << endl;} 

                        else if (old->pagedOut == 1) {
                            
                           if (oflag) {cout << " OUT" << endl;}
                            p[newframe->pid].stat.out++;
                            
                        } 
                    }

                    //reset old pte (if pagedout then it can not be reset) 
                     reset_pte(old);
                    
                } 

                //set frame of new pte to newframe
                pt->frameIndex = newframe->f_index;
                
                // update newframe
                newframe->is_mapped = true;
                newframe->vpage = g.second;
                newframe->pid = curr_process;
                //set valid bit of new pte
                pt->present = 1;
                
                // if this new vpage has been accessed before.
                   if (pt->isFileMapped) {p[curr_process].stat.fin++; if (oflag) cout << " FIN" << endl;}
                   else if (pt->pagedOut) {p[curr_process].stat.in++; if (oflag) cout << " IN" << endl;}  
                    else {p[curr_process].stat.zero++; if (oflag) cout << " ZERO" << endl;}
                    p[curr_process].stat.map++;
                    pagermode->reset_age_bit(newframe->f_index);
                    newframe->tlu = inst;
                    if (oflag) cout << " MAP " << newframe->f_index << endl;  
            }
        }
        // check write protection
        // set r and m bits acc to operation
        if (g.first == 'w') {
            
            if (pt->writeProtect) {
                p[curr_process].stat.segprot++;
                if (oflag) cout << " SEGPROT" << endl;
            }
            else pt->modified = 1;
        }
        pt->referenced = 1;
    //print_ft();
    }
}


main(int argc, char* argv[]) {
  
    int c;
  
    while ((c = getopt(argc,argv,"f:a:o:")) != -1 )
     {
         switch(c) {
         case 'f': 
             sscanf(optarg, "%d", &num_frames);
             frame_table.resize(num_frames);
             free_frames.resize(num_frames);
             initialize_frames();
             break;
         case 'a': 
            sscanf(optarg, "%s", &algo);
            getPager(algo);
            break;
         case 'o':
             options = optarg;
             break;
         }
     }
    
    if ((argc - optind) < 2) {
         printf("\nGive me My precious! arguments\n");
         exit(1);
     }
    
    char *in_file = argv[optind++];
    char *r_file = argv[optind];
    read_rfile(r_file);
    read_input_file(in_file);
    
    //check_inputs();
    
    //printing info according to options given
    if (options.find('O') != std::string::npos) oflag = 1;   
    simulation();
    if (options.find('P') != std::string::npos) print_pt();
    if (options.find('F') != std::string::npos) print_ft();
    if (options.find('S') != std::string::npos) print_sum();
}