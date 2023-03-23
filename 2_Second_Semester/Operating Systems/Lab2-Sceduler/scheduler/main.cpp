#include <stdio.h>
#include <iostream>
#include <getopt.h>
#include <deque>
#include <vector>
#include <algorithm>
#include <string.h>

using namespace std;

typedef enum
{
    TO_PRE_PROCESS,
    ALL_COMPLETED,
    SET_READY,
    SET_RUN,
    TO_BLOCK
} process_transition;
typedef enum
{
    PROCESS_CREATED,
    READY,
    RUNNING,
    BLOCKED,
    DONE
} process_state;

class Process
{
public:
    int total_time_remaining;
    process_state process_current_state;
    int time_in_ready_state;
    int current_work_time;
    int final_finishing_time;
    int input_output_total_time;
    int current_left_cb;
    int last_to_start;
    bool from_current_input_output;
    int starting_time;
    int process_ID;
    int dynamic_priority;
    int cal_timer;
    int input_output_burst;
    int process_burst;
    int static_priority;
    int cpu_init_time;
};

class Event
{
public:
    process_transition pt;
    process_state processState;
    Process *proc;
    int currentTime;
};
int verbose = 0;
int finished_count = 0;
int active_current_count = 0;

deque<Process *> *active_runningqueue, *expired_runningqueue;
deque<Process *> runningqueue;
int memquan = 10000;
int maximumpriority = 4;

class Sched
{
public:
    std::string identity;
    virtual Process *gettingProc() {}
    virtual void addingProc(Process *proc) {}
};

// void printLineNum(int currentCount) {
//     if(currentCount < 10) {
//         cout<<"00"<<currentCount;
//     } else if(currentCount >= 10 && currentCount < 100) {
//         cout<<"0"<<currentCount;
//     } else {
//         cout<<currentCount;
//     }
//     return;
// }

class PRIOscheduler : public Sched
{
public:
    void secondInternalCallProc(Process *proc)
    {
        if (verbose == 1)
        {
            int k = 0;
            while (k < maximumpriority)
            {
                cout << "Prio level " << k << " :\t";
                int l = 0;
                while (l < active_runningqueue[k].size())
                {
                    cout << active_runningqueue[k][l]->process_ID << "\t";
                    l++;
                }
                cout << endl;
                k++;
            }
        }
    }
    void firstSystemCallProc(Process *proc)
    {
        if (proc->dynamic_priority != -1)
        {
            active_runningqueue[proc->dynamic_priority].push_back(proc);
            active_current_count += 1;
        }
        else
        {
            proc->dynamic_priority = proc->static_priority - 1;
            expired_runningqueue[proc->dynamic_priority].push_back(proc);
            finished_count += 1;
        }
        secondInternalCallProc(proc);

        if (verbose == 1)
            cout << endl;
    }
    Process *SecondSysInternalCallTwo(int j)
    {

        while (j >= 0)
        {
            if (!active_runningqueue[j].empty())
            {
                Process *temporary = active_runningqueue[j].front();
                active_runningqueue[j].pop_front();
                active_current_count = active_current_count - 1;
                return temporary;
            }
            j--;
        }
    }
    void SecondSysInternalCallOne(bool check_for_empty)
    {
        if (check_for_empty)
        {
            if (verbose == 1)
                cout << "SWAPPED" << endl;
            deque<Process *> *temporary = active_runningqueue;
            active_runningqueue = expired_runningqueue;
            expired_runningqueue = temporary;

            int temp = active_current_count;
            active_current_count = finished_count;
            finished_count = temp;
        }
        if (verbose == 1)
            cout << endl;
    }
    void addingProc(Process *proc)
    {
        firstSystemCallProc(proc);
    }
    Process *SecondSysCallProc(bool check_for_empty)

    {
        int i = maximumpriority - 1;
        while (i >= 0)
        {
            if (!active_runningqueue[i].empty())
            {
                check_for_empty = false;
                break;
            }
            i--;
            if (!active_runningqueue[i].empty())
                cout << endl;
        }
        SecondSysInternalCallOne(check_for_empty);
        int j = maximumpriority - 1;
        Process *SysTwoFinalCall = SecondSysInternalCallTwo(j);
        return SysTwoFinalCall;

        if (!active_runningqueue[j].empty())
            cout << endl;
    }
    Process *gettingProc()
    {
        bool check_for_empty = true;
        Process *sysCallFinal = SecondSysCallProc(check_for_empty);
        return sysCallFinal;
    }
};

class RR_Scheduler : public Sched
{
public:
    Process *sysCallOne()
    {

        if (runningqueue.empty())
        {
            return nullptr;
        }
        else
        {
            Process *temporary = runningqueue.front();
            runningqueue.pop_front();
            return temporary;
        }
    }
    void addingProc(Process *proc)
    {
        runningqueue.push_back(proc);
    }

    Process *gettingProc()
    {
        Process *callFinal = sysCallOne();
        return callFinal;
    }
};

class FCFSsched : public Sched
{
public:
    void addingProc(Process *proc)
    {
        runningqueue.push_back(proc);
    }

    Process *firstEnum()
    {
        if (runningqueue.empty())
        {
            return nullptr;
        }
        else
        {
            Process *temporary = runningqueue.front();
            runningqueue.pop_front();
            return temporary;
        }
    }
    Process *gettingProc()
    {
        Process *inVal = firstEnum();
        return inVal;
    }
};
class SRTF_scheduler : public Sched
{
public:
    Process *EnumerationTwo()

    {
        if (runningqueue.empty())
        {
            return nullptr;
        }
        else
        {
            Process *temporary = runningqueue.front();
            runningqueue.pop_front();
            return temporary;
        }
    }
    void EnumerationOne(Process *proc)
    {
        if (runningqueue.empty())
        {
            runningqueue.push_back(proc);
        }
        else
        {

            int i = 0;
            int dirIndex = runningqueue.size();
            while (i < runningqueue.size())
            {
                if (proc->total_time_remaining < runningqueue[i]->total_time_remaining)
                {
                    dirIndex = i;
                    break;
                }
                i++;
            }

            runningqueue.insert(runningqueue.begin() + dirIndex, proc);
        }
    }

    void addingProc(Process *proc)
    {
        EnumerationOne(proc);
    }

    Process *gettingProc()
    {
        Process *inval = EnumerationTwo();
        return inval;
    }
};

class LCFS_scheduler : public Sched
{
public:
    Process *firstEnum()
    {
        if (runningqueue.empty())
        {
            return nullptr;
        }
        else
        {
            Process *temporary = runningqueue.back();
            runningqueue.pop_back();
            return temporary;
        }
    }
    Process *gettingProc()
    {
        Process *inval = firstEnum();
        return inval;
    }
    void addingProc(Process *proc)
    {
        runningqueue.push_back(proc);
    }
};

class PREPRIO_scheduler : public PRIOscheduler
{
public:
    void addingProc(Process *proc)
    {
        PRIOscheduler::addingProc(proc);
    }
    Process *gettingProc()
    {
        return PRIOscheduler::gettingProc();
    }
};

// void printInstructionAddress(int instructionAddress) {
//     if(instructionAddress < 10) {
//         cout<<"000"<<instructionAddress;
//     } else if(instructionAddress >= 10 && instructionAddress < 100) {
//         cout<<"00"<<instructionAddress;
//     } else if(instructionAddress >= 100 && instructionAddress < 1000){
//         cout<<"0"<<instructionAddress;
//     } else {
//         cout<<instructionAddress;
//     }
//     return;
// }

Sched *schemamode;

std::string getSched(char ch)
{
    if (ch == 'L')
    {
        schemamode = new LCFS_scheduler();
        return "LCFS";
    }
    else if (ch == 'S')
    {
        schemamode = new SRTF_scheduler();
        return "SRTF";
    }
    else if (ch == 'E')
    {
        schemamode = new PREPRIO_scheduler();
        return "PREPRIO";
    }

    else if (ch == 'F')
    {
        schemamode = new FCFSsched();
        return "FCFS";
    }
    else if (ch == 'R')
    {
        schemamode = new RR_Scheduler();
        return "RR";
    }
    else if (ch == 'P')
    {
        schemamode = new PRIOscheduler();
        return "PRIO";
    }
}

char sched;
int r_file_length;
int currentOffset = -1;
Sched myCurr_working_sched_;
vector<int> randomIntervals;
vector<Process> p;
deque<Event *> currEvent;

int prePrintCallOne(int systemCurrentBurst)
{
    currentOffset = (currentOffset + 1) % r_file_length;
    int burstfinal;
    burstfinal = (randomIntervals[currentOffset] % systemCurrentBurst) + 1;
    return burstfinal;
}
int myrandom(int systemCurrentBurst)
{
    int callPostPrint = prePrintCallOne(systemCurrentBurst);
    return callPostPrint;
}
void prePrintRead(char *currentfilelocation, int linenumberfull, FILE *tot_file)
{
    char currlinespc[50];

    while (fgets(currlinespc, sizeof(currlinespc), tot_file) != NULL)
    {
        if (linenumberfull != 0)
        {
            randomIntervals.push_back(atoi(strtok(currlinespc, " \t\n")));
        }
        else
        {
            r_file_length = atoi(strtok(currlinespc, " \t\n"));
            linenumberfull++;
        }
    }
    fclose(tot_file);
}
void read_totrFile(char *currentfilelocation)
{
    FILE *tot_file = fopen(currentfilelocation, "r");
    int linenumberfull = 0;
    prePrintRead(currentfilelocation, linenumberfull, tot_file);
}
void prePrintCall(char *currentfilelocation, FILE *current_ip_file, int currentLineNumber, char *tkn)
{
    char lineLength[100];
    while (fgets(lineLength, sizeof(lineLength), current_ip_file) != NULL)
    {
        Process process01;
        currentLineNumber++;
        process01.process_ID = currentLineNumber;
        process01.starting_time = atoi(strtok(lineLength, " \t\n"));
        process01.cpu_init_time = atoi(strtok(NULL, " \t\n"));
        process01.process_burst = atoi(strtok(NULL, " \t\n"));
        process01.input_output_burst = atoi(strtok(NULL, " \t\n"));
        process01.last_to_start = 0;
        process01.current_work_time = 0;
        process01.input_output_total_time = 0;
        process01.current_left_cb = 0;
        process01.static_priority = myrandom(maximumpriority);
        process01.dynamic_priority = process01.static_priority - 1;
        process01.total_time_remaining = process01.cpu_init_time;
        process01.final_finishing_time = process01.starting_time;

        process01.process_current_state = PROCESS_CREATED;
        process01.from_current_input_output = false;
        p.push_back(process01);
    }
}
void read_all_ip_file(char *currentfilelocation)
{
    FILE *current_ip_file = fopen(currentfilelocation, "r");
    char *tkn;
    int currentLineNumber = -1;
    prePrintCall(currentfilelocation, current_ip_file, currentLineNumber, tkn);
    fclose(current_ip_file);
}

Event *get_event()
{
    if (currEvent.empty())
    {
        return nullptr;
    }
    else
    {
        Event *first = currEvent.front();
        currEvent.pop_front();
        return first;
    }
}

void put_event(Event *currentEvent)
{

    int dirIndex = currEvent.size(), i = 0;
    while (i < currEvent.size())
    {
        if (currentEvent->currentTime < currEvent[i]->currentTime)
        {
            dirIndex = i;
            break;
        }
        i++;
    }
    currEvent.insert(currEvent.begin() + dirIndex, currentEvent);
}
bool processPrePrintCheck(Process *proc, int tkn, int i)
{
    if (tkn > currEvent[i]->currentTime)
        return false;
    while (tkn <= currEvent[i]->currentTime)
    {
        if ((currEvent[i]->currentTime == tkn))
        {
            if ((currEvent[i]->proc->process_ID == proc->process_ID))
                return true;
        }
        i++;
        if (i == currEvent.size())
            break;
    }

    return false;
}
bool check_currEvent(Process *proc, int tkn)
{
    int i = 0;
    bool PrePrintCheckOne = processPrePrintCheck(proc, tkn, i);
    return PrePrintCheckOne;
}

int trace_input_output_start = 0;
bool CALL_IN_SCHEDULER = false;
Process *current_running_process = nullptr;
double total_input_out_avail = 0.0;
int trace_input_output_process = 0;

void inSchedulerPrePrintCheck(Event *current_event_track, int current_time_instance, Process *current_event_process)
{
    if (current_running_process == nullptr)
    {
        if ((sched != 'P'))
        {
            if (sched != 'E')
            {
                if (!runningqueue.empty())
                {
                    current_running_process = schemamode->gettingProc();
                    Event *eventn1 = new Event;
                    eventn1->processState = READY;
                    eventn1->pt = SET_RUN;
                    eventn1->currentTime = current_time_instance;
                    eventn1->proc = current_running_process;
                    put_event(eventn1);
                }
            }
        }
    }
}

void setBlockPrePrintCheck(Event *current_event_track, int current_time_instance, Process *current_event_process)
{
    current_event_process->from_current_input_output = true;

    int inputoutput_time_block = myrandom(current_event_process->input_output_burst);

    if (verbose == 1)
    {
        cout << current_time_instance << " Process "
             << current_event_process->process_ID << " from "
             << current_event_track->processState << " to block state io = "
             << inputoutput_time_block << " prio = " << current_event_process->dynamic_priority << endl;
    }
    if (verbose == 1)
        cout << endl;
    int callfinalvalue = current_time_instance + inputoutput_time_block;

    current_event_process->last_to_start = callfinalvalue;
    current_event_process->final_finishing_time = inputoutput_time_block + current_event_process->final_finishing_time;
    current_event_process->input_output_total_time = inputoutput_time_block + current_event_process->input_output_total_time;
    current_event_process->process_current_state = BLOCKED;

    Event *eventform2 = new Event;
    eventform2->pt = SET_READY;
    eventform2->processState = BLOCKED;
    eventform2->currentTime = callfinalvalue;
    eventform2->proc = current_event_process;

    put_event(eventform2);
    CALL_IN_SCHEDULER = true;
    current_running_process = nullptr;
}
void inSchedulerPostPrintCall(Event *current_event_track, int current_time_instance, Process *current_event_process)
{
    if (current_running_process == nullptr)
    {
        if (sched == 'P' || sched == 'E')
        {
            if (active_current_count != 0 || finished_count != 0)
            {
                current_running_process = schemamode->gettingProc();
                if (verbose == 1)
                {
                    int k = 0;
                    while (k < maximumpriority)
                    {
                        cout << "Prio level "
                             << k << " :\t";
                        int l = 0;
                        while (l < active_runningqueue[k].size())
                        {
                            cout << active_runningqueue[k][l]->process_ID << "\t";
                            l++;
                        }
                        cout << endl;
                        k++;
                    }
                }
                if (verbose == 1)
                    cout << endl;
                Event *eventn2 = new Event;
                eventn2->processState = READY;
                eventn2->pt = SET_RUN;
                eventn2->currentTime = current_time_instance;
                eventn2->proc = current_running_process;
                put_event(eventn2);
            }
        }
    }
}

void setRunPrePrintCheck(Event *current_event_track, int current_time_instance, Process *current_event_process, int checkTime, int given_initiating_cpu_time)
{
    int minVal = min(given_initiating_cpu_time, current_event_process->total_time_remaining);
    minVal = min(memquan, minVal);

    if (minVal == current_event_process->total_time_remaining)
    {
        if (verbose == 1)
        {
            cout << current_time_instance << " Process " << current_event_process->process_ID << " from " << current_event_track->processState << " to running state cb = "
                 << current_event_process->total_time_remaining << " prio = " << current_event_process->dynamic_priority << endl;
        }
        int instance01 = current_event_process->total_time_remaining;
        current_time_instance = instance01 + current_time_instance;
        current_event_process->final_finishing_time = current_time_instance;
        current_event_process->current_left_cb = 0;
        current_event_process->total_time_remaining = 0;
        Event *element1 = new Event;
        element1->processState = DONE;
        element1->pt = ALL_COMPLETED;
        element1->currentTime = current_time_instance;
        element1->proc = current_event_process;

        put_event(element1);
    }
    else if (minVal == given_initiating_cpu_time)
    {

        if (verbose == 1)
        {
            cout << current_time_instance << " Process " << current_event_process->process_ID << " from " << current_event_track->processState << " to running state cb = "
                 << given_initiating_cpu_time << " rem = " << current_event_process->total_time_remaining << " prio = " << current_event_process->dynamic_priority << endl;
        }
        int valueforIns = given_initiating_cpu_time;

        current_event_process->total_time_remaining = current_event_process->total_time_remaining - valueforIns;
        current_time_instance = valueforIns + current_time_instance;
        current_event_process->final_finishing_time = valueforIns + current_event_process->final_finishing_time;
        current_event_process->current_left_cb = 0;
        Event *element2 = new Event;
        element2->processState = RUNNING;
        element2->pt = TO_BLOCK;
        element2->proc = current_event_process;
        element2->currentTime = current_time_instance;

        put_event(element2);
    }
    else
    {
        if (verbose == 1)
        {
            cout << current_time_instance << " Process " << current_event_process->process_ID << " from "
                 << current_event_track->processState << " to running state cb = " << given_initiating_cpu_time << " rem = "
                 << current_event_process->total_time_remaining << " prio = " << current_event_process->dynamic_priority << endl;
        }
        int tempSave = memquan;
        current_event_process->total_time_remaining = current_event_process->total_time_remaining - tempSave;
        current_time_instance = tempSave + current_time_instance;
        current_event_process->final_finishing_time = tempSave + current_event_process->final_finishing_time;
        current_event_process->current_left_cb = current_event_process->current_left_cb - tempSave;

        Event *element3 = new Event;
        element3->proc = current_event_process;
        element3->currentTime = current_time_instance;
        element3->processState = RUNNING;
        element3->pt = TO_PRE_PROCESS;
        put_event(element3);
    }
    current_event_process->last_to_start = current_time_instance;
}
double totalTime = 0.0;
double waiting_tot_time = 0.0;
double total_cpu_utilization = 0.0;

void printTotalUtilization()
{
    int i = 0;
    while (i < p.size())

    {
        int st = p[i].starting_time;
        int cpuinitialize = p[i].cpu_init_time;
        int currwork = p[i].current_work_time;
        int ft = p[i].final_finishing_time;
        int differenceInTime = ft - st;
        waiting_tot_time = currwork + waiting_tot_time;
        totalTime = differenceInTime + totalTime;
        total_cpu_utilization = cpuinitialize + total_cpu_utilization;

        printf("%04d: %4d %4d %4d %4d %1d | %5d %5d %5d %5d\n",
               p[i].process_ID, st, cpuinitialize,
               p[i].process_burst, p[i].input_output_burst, p[i].static_priority,
               ft, differenceInTime, p[i].input_output_total_time,
               currwork);

        i++;
    }
}
void printPostFinalSimulations(int current_time_instance)
{
    int simulations_completed = current_time_instance;
    printf("SUM: %d %.2lf %.2lf %.2lf %.2lf %.3lf\n", simulations_completed,
           100.0 * (total_cpu_utilization / simulations_completed), 100.0 * (total_input_out_avail / simulations_completed),
           totalTime / p.size(), waiting_tot_time / p.size(), (double)p.size() / (simulations_completed / 100.0));
}

void setReadyPrePrintCheck(Event *current_event_track, int current_time_instance, Process *current_event_process)
{
    if (sched == 'E')
    {
        if (current_event_process->process_current_state == BLOCKED || current_event_process->process_current_state == PROCESS_CREATED)
        {
            if (current_running_process != nullptr)
            {
                if (current_event_process->dynamic_priority > current_running_process->dynamic_priority)
                {
                    if ((!check_currEvent(current_running_process, current_time_instance)))
                    {
                        int incr = 0;

                        while (true)
                        {
                            if (currEvent[incr]->proc->process_ID == current_running_process->process_ID)
                            {
                                currEvent.erase(currEvent.begin() + incr);
                                break;
                            }
                            if (currEvent[incr]->proc->process_ID == current_running_process->process_ID)
                                cout << endl;
                            incr++;
                        }
                        Event *elementInQueue = new Event;
                        elementInQueue->pt = TO_PRE_PROCESS;
                        elementInQueue->processState = READY;
                        elementInQueue->currentTime = current_time_instance;
                        elementInQueue->proc = current_running_process;
                        put_event(elementInQueue);

                        int ansFinalCheck = current_time_instance - current_running_process->last_to_start;

                        current_running_process->total_time_remaining = current_running_process->total_time_remaining - ansFinalCheck;
                        current_running_process->final_finishing_time = ansFinalCheck + current_running_process->final_finishing_time;
                        current_running_process->current_left_cb = current_running_process->current_left_cb - ansFinalCheck;
                    }
                }
            }
        }
    }
    current_event_process->time_in_ready_state = current_time_instance;
    current_event_process->process_current_state = READY;
    CALL_IN_SCHEDULER = true;

    schemamode->addingProc(current_event_process);

    if (verbose == 1)
    {
        cout << current_time_instance << " Process " << current_event_process->process_ID << " from "
             << current_event_track->processState << " to ready state"
             << " rem " << current_event_process->total_time_remaining << " prio = "
             << current_event_process->dynamic_priority << endl;
    }
    if (verbose == 1)
        cout << endl;
}

void setToPreProcessPrePrintCheck(Event *current_event_track, int current_time_instance, Process *current_event_process)
{
    if (verbose == 1)
        cout << endl;

    current_event_process->time_in_ready_state = current_time_instance;
    current_event_process->process_current_state = READY;
    current_event_process->dynamic_priority = current_event_process->dynamic_priority - 1;
    current_running_process = nullptr;

    schemamode->addingProc(current_event_process);

    CALL_IN_SCHEDULER = true;
}

void postPrintfile1(int character, int argcAccept, int argc, char *argv[])
{
    while ((character = getopt(argc, argv, "vtes:")) != -1)
    {

        switch (character)
        {
        case ':':
            printf("Provide option value!\n");
            break;
        case '?':
            cout << "Unrecognized option\n";
            break;
        case 'e':
            break;
        case 't':
            break;
        case 'v':
            verbose = 1;
            break;

        case 's':
            sscanf(optarg, "%s", &sched);
            myCurr_working_sched_.identity = getSched(sched);
            sscanf(optarg + 1, "%d:%d", &memquan, &maximumpriority);
            active_runningqueue = new deque<Process *>[maximumpriority];
            expired_runningqueue = new deque<Process *>[maximumpriority];
            break;
        }
    }
}
void postPrintfile2(int argcAccept, char *argv[])
{
    if (argcAccept < 2)
    {
        printf("\nGive me My precious! arguments\n");
        exit(1);
    }
    if (argcAccept < 2)
        cout << endl;

    char *in_file = argv[optind++];
    char *tot_file = argv[optind];
    read_totrFile(tot_file);
    read_all_ip_file(in_file);
}
// void checkUseOfSymbolsForInstructionSet(vector<string> useList, vector<string> usedSymbol, int moduleIterator, Module &module) {
//     for(int i = 0; i < useList.size(); i++) {
//         if(!isInVector(usedSymbol, useList[i])) {
//             Warning w;
//             w.setCode(7);
//             w.setModuleNumber(moduleIterator);
//             w.setSymbol(useList[i]);
//             module.addWarningToList(w);
//         }
//     }
// }

void simulation()
{
    Event *current_event_track;
    int current_time_instance = p[0].starting_time;
    while ((current_event_track = get_event()))
    {
        Process *current_event_process = current_event_track->proc;

        current_time_instance = current_event_track->currentTime;

        switch (current_event_track->pt)
        {
        case SET_READY:
        {
            if (current_event_process->from_current_input_output == true)
            {
                current_event_process->from_current_input_output = false;
                current_event_process->dynamic_priority = current_event_process->static_priority - 1;
                trace_input_output_process = trace_input_output_process - 1;
                if (trace_input_output_process == 0)
                {
                    int ans = current_time_instance - trace_input_output_start;
                    total_input_out_avail = ans + total_input_out_avail;
                }
            }
            setReadyPrePrintCheck(current_event_track, current_time_instance, current_event_process);
            break;
        }
        case SET_RUN:
        {

            current_running_process = current_event_process;

            current_event_process->process_current_state = RUNNING;
            int checkTime = current_time_instance - current_event_process->time_in_ready_state;
            current_event_process->current_work_time = checkTime + current_event_process->current_work_time;

            int given_initiating_cpu_time;
            if (current_event_process->current_left_cb != 0)
            {
                given_initiating_cpu_time = current_event_process->current_left_cb;
            }
            else
            {
                given_initiating_cpu_time = myrandom(current_event_process->process_burst);
                current_event_process->current_left_cb = given_initiating_cpu_time;
            }
            setRunPrePrintCheck(current_event_track, current_time_instance, current_event_process, checkTime, given_initiating_cpu_time);

            break;
        }
        case TO_BLOCK:
        {
            if (trace_input_output_process == 0)
            {
                trace_input_output_start = current_time_instance;
            }
            trace_input_output_process++;
            setBlockPrePrintCheck(current_event_track, current_time_instance, current_event_process);
            break;
        }

        case TO_PRE_PROCESS:
        {
            if (verbose == 1)
            {
                cout << current_time_instance << " Preempting process " << current_event_process->process_ID << " from " << current_event_track->processState << " \n";
            }
            setToPreProcessPrePrintCheck(current_event_track, current_time_instance, current_event_process);
            break;
        }
        case ALL_COMPLETED:
        {
            if (verbose == 1)
            {
                cout << "Process " << current_running_process->process_ID << " Done" << endl;
            }
            if (verbose == 1)
                cout << endl;
            CALL_IN_SCHEDULER = true;
            current_running_process = nullptr;
        }
        }
        delete current_event_track;
        current_event_track = nullptr;
        if (CALL_IN_SCHEDULER == true)
        {
            if (!currEvent.empty())
            {
                if (currEvent[0]->currentTime == current_time_instance)
                {
                    continue;
                }
                if (currEvent[0]->currentTime == current_time_instance)
                {
                    cout << endl;
                }
            }
            CALL_IN_SCHEDULER = false;
            inSchedulerPrePrintCheck(current_event_track, current_time_instance, current_event_process);
            inSchedulerPostPrintCall(current_event_track, current_time_instance, current_event_process);
        }
    }
    if (memquan != 10000)
    {
        cout << myCurr_working_sched_.identity << " " << memquan << endl;
    }
    else
    {
        cout << myCurr_working_sched_.identity << endl;
    }
    printTotalUtilization();
    printPostFinalSimulations(current_time_instance);
}
main(int argc, char *argv[])
{
    int character;
    int argcAccept = argc - optind;
    postPrintfile1(character, argcAccept, argc, argv);
    postPrintfile2(argcAccept, argv);

    int i = 0;
    while (i < p.size())
    {
        Event *eventmain1 = new Event;
        eventmain1->processState = PROCESS_CREATED;
        eventmain1->pt = SET_READY;
        eventmain1->currentTime = p[i].starting_time;
        eventmain1->proc = &p[i];
        currEvent.push_back(eventmain1);
        i++;
    }
    simulation();
}