#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <iomanip>
#include <map>
#include <set>
#include <queue>
#include <stack>
#include <algorithm>
#include <math.h>
#include <string.h>
#include <list>
#include <stdio.h>
#include <unistd.h>
#include <climits>

using namespace std;
#define MAX_QUANTUM_TS INT_MAX

ifstream randomFile;
ifstream inputFile;
string inputFileName;
string randomFileName;
bool verbose = false;
string schedulerType = "", schedulerOptionPrint = "";

void error_handler();

enum STATUS
{
    CREATED,
    READY,
    RUNNING,
    BLOCKED,
    FINISHED
};

class Process
{
public:
    //attributes for the Process
    int processId;
    int arrivalTime;
    int maxCpuBurst;
    int maxIOBurst;
    int totalCpuTime;
    int currentCpuBurst = 0;
    int static_priority;

    // need to calculate the cpu/io times left directly
    //dynamic properties that change
    int cpuTimeRemaining = 0;
    int ioTimeRemaining = 0;
    int cpuBurstRemaining = 0;
    int ioBurstRemaining = 0;
    int priority = 0;
    int state_ts;
    STATUS status;

    //required for stats
    int creationTime = 0;
    int finishingTime = 0;
    int waitTime = 0;
    int ioTime = 0;
    int turnAroundTime = 0;

    //init process
    Process(int id, int at, int tc, int cb, int io, int prio)
    {
        //process id as id
        this->processId = id;
        //arrival time as at, initial timestamp for process is at
        this->arrivalTime = at;
        this->state_ts = at;
        //Total CPU Time and CPU Time Remaining both as tc initially
        this->totalCpuTime = tc;
        this->cpuTimeRemaining = tc;
        //Max CPU Burst as cb
        this->maxCpuBurst = cb;
        //Max IO Burst as io
        this->maxIOBurst = io;
        //Before in running, cpuBurstRemaining is 0, ioBurstRemaining is 0
        this->cpuBurstRemaining = 0;
        this->ioBurstRemaining = 0;
        //Status of CREATED when Process created
        this->status = CREATED;
        //Static priority, (max_priority+1) for process is prio
        this->static_priority = prio;
        //IO Time and Wait Time for stats, intially as 0
        this->ioTime = 0;
        this->waitTime = 0;
        //Initial priority set as max_prio = static_priority - 1
        this->priority = prio - 1;
    }
};

//default max priority level of 4
int maxPrios = 4;

//Schedulers defined here
// Base IScheduler
// FCFS Scheduler
// LCFS Scheduler

//general scheduler interface
class IScheduler
{
public:
    virtual ~IScheduler(){};
    virtual void add_process(Process *p) = 0;
    virtual Process *get_next_process() = 0;
    virtual bool test_preempt(Process *p, int currTime) = 0;
};

//First-Come-First-Server Scheduler
//Uses Ischeduler as the Base Class
//Uses queue as the to get process details
class FCFSScheduler : public IScheduler
{
public:
    FCFSScheduler() {}

    void add_process(Process *p)
    {
        processQueue.push(p);
    }

    Process *get_next_process()
    {
        if (processQueue.empty())
        {
            return nullptr;
        }
        //extract first element
        Process *p = processQueue.front();
        processQueue.pop();
        return p;
    }

    bool test_preempt(Process *p, int currTime)
    {
        return false;
    }

private:
    queue<Process *> processQueue;
};

//Last Come First Serve Scheduler
//Uses Stack
class LCFSScheduler : public IScheduler
{
public:
    LCFSScheduler() {}

    void add_process(Process *p)
    {
        processStack.push(p);
    }

    Process *get_next_process()
    {
        if (processStack.empty())
        {
            return nullptr;
        }
        //extract first element
        Process *p = processStack.top();
        processStack.pop();
        return p;
    }

    bool test_preempt(Process *p, int currTime)
    {
        return false;
    }

private:
    stack<Process *> processStack;
};

//Round Robin Scheduler
//uses a queue to check processes, as it allows for a FCFS type input
class RRScheduler : public IScheduler
{
public:
    RRScheduler(int quantum)
    {
        this->quantum = quantum;
    }

    void add_process(Process *p)
    {
        p->priority = p->priority - 1;
        processQueue.push(p);
    }

    Process *get_next_process()
    {
        if (processQueue.empty())
        {
            return nullptr;
        }
        else
        {
            Process *p = processQueue.front();
            processQueue.pop();
            return p;
        }
    }

    bool test_preempt(Process *p, int currTime)
    {
        //TODO : Add the correct logic with quantum
        if (currTime != -1 && p->cpuBurstRemaining > quantum)
        {
            return true;
        }
        return false;
    }

private:
    int quantum;
    queue<Process *> processQueue;
};

// SRTF Scheduler
// a job is first if:
// 1) It has smallest running time left
// 2) or it is first in queue in case of tie

struct SRTFGreaterElementComparator
{
    bool operator()(pair<int, Process *> p1, pair<int, Process *> p2)
    {
        if (p1.second->cpuTimeRemaining != p2.second->cpuTimeRemaining)
        {
            return p1.second->cpuTimeRemaining > p2.second->cpuTimeRemaining;
        }
        else
        {
            return p1.first > p2.first;
        }
    }
};

class SRTFScheduler : public IScheduler
{
public:
    void add_process(Process *p)
    {
        //should I use the process id or not, upto check
        processQueue.push(make_pair(id, p));
        id++;
    }

    Process *get_next_process()
    {
        if (processQueue.empty())
        {
            return nullptr;
        }
        Process *p = processQueue.top().second;
        processQueue.pop();
        return p;
    }

    bool test_preempt(Process *p, int currTime)
    {
        return false;
    }

private:
    int id;
    priority_queue<pair<int, Process *>, vector<pair<int, Process *>>, SRTFGreaterElementComparator> processQueue;
};

//priority scheduler PRIO
//needs both active and expired queue
class PRIOScheduler : public IScheduler
{
public:
    PRIOScheduler(int prios, int quantum)
    {
        //implement and initialize
        this->prios = prios;
        this->quantum = quantum;
        activeQueue.resize(prios);
        expiredQueue.resize(prios);
    }

    void add_process(Process *p)
    {
        if (p->priority == -1)
        {
            p->priority = p->static_priority - 1;
            expiredQueue[p->priority].push(p);
        }
        else
        {
            activeQueue[p->priority].push(p);
        }
    }

    Process *get_next_process()
    {
        //if ready queue is empty switch to expired queue
        //otherwise return the first element which is present with highest priority
        for (int i = prios - 1; i >= 0; i--)
        {
            if (!activeQueue[i].empty())
            {
                //case where something exists in the ith slot
                Process *p = activeQueue[i].front();
                activeQueue[i].pop();
                return p;
            }
        }
        //swap active and expired when empty
        swap(activeQueue, expiredQueue);

        //recheck the expired queue
        for (int i = prios - 1; i >= 0; i--)
        {
            if (!activeQueue[i].empty())
            {
                //case where something exists in the ith slot
                Process *p = activeQueue[i].front();
                activeQueue[i].pop();
                return p;
            }
        }
        return nullptr;
    }

    bool test_preempt(Process *p, int currTime)
    {
        //TODO : Add the current pre empting logic for PRIO
        if (currTime != -1 && p->cpuBurstRemaining > quantum)
        {
            return true;
        }
        return false;
    }

private:
    int prios;
    int quantum;
    vector<queue<Process *>> activeQueue;
    vector<queue<Process *>> expiredQueue;
};

//Pre PRIO Scheduler
//same as PRIO except in the pre_empt condition
class PREPRIOScheduler : public IScheduler
{
public:
    PREPRIOScheduler(int prios, int quantum)
    {
        //implement and initialize
        this->prios = prios;
        this->quantum = quantum;
        activeQueue.resize(prios);
        expiredQueue.resize(prios);
    }

    void add_process(Process *p)
    {
        if (p->priority == -1)
        {
            p->priority = p->static_priority - 1;
            expiredQueue[p->priority].push(p);
        }
        else
        {
            activeQueue[p->priority].push(p);
        }
    }

    Process *get_next_process()
    {
        //if ready queue is empty switch to expired queue
        //otherwise return the first element which is present with highest priority
        for (int i = prios - 1; i >= 0; i--)
        {
            if (!activeQueue[i].empty())
            {
                //case where something exists in the ith slot
                Process *p = activeQueue[i].front();
                activeQueue[i].pop();
                return p;
            }
        }
        //swap active and expired when empty
        swap(activeQueue, expiredQueue);

        //recheck the expired queue
        for (int i = prios - 1; i >= 0; i--)
        {
            if (!activeQueue[i].empty())
            {
                //case where something exists in the ith slot
                Process *p = activeQueue[i].front();
                activeQueue[i].pop();
                return p;
            }
        }
        return nullptr;
    }

    bool test_preempt(Process *p, int currTime)
    {
        //TODO : Add the current pre empting logic for PREPRIO
        // check if there is another added process with a higher priority
        // or check the quantum scenario as in RR and PRIO
        int curr_priority = p->priority;
        Process *newProcess = nullptr;
        for (int i = prios - 1; i > curr_priority; i--)
        {
            if (!activeQueue[i].empty())
            {
                return true;
            }
        }
        //currTime set as -1 in case preemption is based on non-quantum checks
        if (currTime != -1 && p->cpuBurstRemaining > quantum)
        {
            return true;
        }

        return false;
    }

private:
    int prios;
    int quantum;
    vector<queue<Process *>> activeQueue;
    vector<queue<Process *>> expiredQueue;
};

//Event defined in this section

//global event id to keep track of number of events created
int globalEventId = 0;

enum EVENT_ACTION
{
    TRANS_TO_READY,
    TRANS_TO_RUN,
    TRANS_TO_PREEMPT,
    TRANS_TO_BLOCK,
    TRANS_TO_FINISH
};

class Event
{
private:
    int eventId = 0;

public:
    EVENT_ACTION transition;
    Process *evtProcess;
    int evtTimeStamp;
    Event() {}
    Event(EVENT_ACTION action,
          Process *p,
          int timestamp)
    {
        this->eventId = globalEventId;
        globalEventId++;
        this->transition = action;
        this->evtProcess = p;
        this->evtTimeStamp = timestamp;
    }

    //custom comparator to check the event times
    //check first the timestamp which is lesser
    //in case of tie, break using event id (first declared)
    bool operator<(const Event &e)
    {
        if (this->evtTimeStamp != e.evtTimeStamp)
        {
            return this->evtTimeStamp < e.evtTimeStamp;
        }
        return this->eventId < e.eventId;
    }

    bool operator>(const Event &e)
    {
        if (this->evtTimeStamp != e.evtTimeStamp)
        {
            return this->evtTimeStamp > e.evtTimeStamp;
        }
        return this->eventId > e.eventId;
    }
};

//comparator for vector insert
struct greaterEvent
{
    bool operator()(Event *e1, Event *e2)
    {
        return (*e2) < (*e1);
    }
};

//Event Simulator described in this section

class EventSimulator
{
private:
    IScheduler *scheduler;
    int randPos;
    int quantum;
    vector<Event *> eventQueue;
    int nextIOEndTime;
    int randomOffset = 0;
    vector<long long int> randomVector;

public:
    int lastEventFinish, cpuOccupied, ioOccupied, newxtIOEnd;
    EventSimulator(IScheduler *scheduler, string randFileName, int quantum)
    {
        this->scheduler = scheduler;
        this->quantum = quantum;
        lastEventFinish = 0;
        cpuOccupied = 0;
        ioOccupied = 0;
        newxtIOEnd = 0;
        randPos = 0;
        init_random_array(randFileName);
    }

    //add event to event queue used by scheduler to access next event
    void add_event(Event *evt)
    {
        //add event in order
        if (upper_bound(eventQueue.begin(), eventQueue.end(), evt, [](Event *e1, Event *e2)
                        { return (*e2) > (*e1); }) == eventQueue.end())
        {
            eventQueue.push_back(evt);
            return;
        }
        eventQueue.insert(
            upper_bound(eventQueue.begin(), eventQueue.end(), evt, [](Event *e1, Event *e2)
                        { return (*e2) > (*e1); }),
            evt);
        return;
    }

    //get next event in case of scheduling change
    Event *get_event()
    {
        if (eventQueue.empty())
        {
            return nullptr;
        }
        Event *evt = eventQueue.front();
        eventQueue.erase(eventQueue.begin());
        return evt;
    }

    //remove event pertaining to a particular Process
    bool remove_event_for_process(Process *p, int currTime)
    {
        for (int i = 0; i < eventQueue.size(); i++)
        {
            if (eventQueue[i]->evtProcess == p && eventQueue[i]->evtTimeStamp > currTime)
            {
                if (verbose)
                {
                    cout << "1 TS=" << eventQueue[i]->evtTimeStamp << " now=" << currTime <<" --> YES" << endl;
                }
                eventQueue.erase(eventQueue.begin() + i);
                return true;
            }
        }
        if (verbose)
        {
            cout << "0 TS=" << currTime << " now=" << currTime <<" --> NO" << endl;
        }
        return false;
    }

    //get next time for the Discrete Simulation, from the Event Time
    int get_next_event_time()
    {
        if (eventQueue.empty())
        {
            return MAX_QUANTUM_TS;
        }
        else
        {
            return eventQueue.front()->evtTimeStamp;
        }
    }

    //random init fucntionalities
    void init_random_array(string randomFileName)
    {
        ifstream file(randomFileName);
        if (!file.is_open())
        {
            error_handler();
        }
        int numofNo;
        file >> numofNo;
        randomVector.resize(numofNo);
        for (int i = 0; i < numofNo; i++)
        {
            file >> randomVector[i];
        }
        file.close();
    }

    //generate random number
    int myrandom(int burst)
    {
        int val = 1 + (randomVector[randomOffset] % burst);
        //increment and wrap around
        randomOffset = (randomOffset + 1) % randomVector.size();
        return val;
    }

    //Simulation code here, based in states resolved in a particular event
    void Simulation()
    {
        Event *evt;
        bool CALL_SCHEDULER = false;
        Process *CURRENT_PROCESS = nullptr;

        while ((evt = get_event()))
        {
            Process *proc = evt->evtProcess; // this is the process the event works on
            int currTime = evt->evtTimeStamp;
            int timeInPrevState = currTime - proc->state_ts;
            switch (evt->transition)
            {
            // which state to transition to?
            case TRANS_TO_READY:
            {
                // must come from BLOCKED or from PREEMPTION
                // must add to run queue

                //Preempted process here
                if (proc->status == RUNNING)
                {
                    proc->cpuBurstRemaining -= timeInPrevState;
                    proc->cpuTimeRemaining -= timeInPrevState;
                    cpuOccupied += timeInPrevState;
                    CURRENT_PROCESS = nullptr;
                    //verbose print
                    if (verbose)
                    {
                        cout << currTime << " " << proc->processId << " " << timeInPrevState << ": RUNNING->READY cb=" << proc->cpuBurstRemaining << " rem=" << proc->cpuTimeRemaining << " prio=" << proc->priority << endl;
                    }
                    //priority shifted by 1 less for process
                    proc->priority -= 1;
                }
                else if (proc->status == BLOCKED)
                {
                    proc->ioBurstRemaining = 0;
                    proc->ioTime += timeInPrevState;
                    //BLOCKED to READY, priority moved to static_prioirty - 1
                    proc->priority = proc->static_priority - 1;
                    //verbose print
                    if (verbose)
                    {
                        cout << currTime << " " << proc->processId << " " << timeInPrevState << " : BLOCKED -> READY" << endl;
                    }
                }
                else if (proc->status == CREATED)
                {
                    //moved from CREATED to READY, printing verbose main use , otherwise no change
                    // Probably PREPRIO pre empting has to be tested here, if current process added
                    // has higher prio than current running process

                    //verbose print
                    if (verbose)
                    {
                        cout << currTime << " " << proc->processId << " " << timeInPrevState << " : CREATED -> READY" << endl;
                    }
                }
                else
                {
                    //This should not happen, and ideally wouldn't but in case error
                    error_handler();
                }

                //preempt logic for current process and EPRIO
                STATUS prevStatus = proc->status;
                proc->status = READY;
                scheduler->add_process(proc);
                CALL_SCHEDULER = true; // conditional on whether something is run
                if (prevStatus == CREATED || prevStatus == BLOCKED)
                {
                    if (CURRENT_PROCESS == nullptr)
                    {
                        //do nothing
                    }
                    else
                    {
                        if (verbose)
                        {
                            cout << "---> PRIO preemption " << CURRENT_PROCESS->priority << " by " << proc->priority << " ? ";
                        }
                        if (scheduler->test_preempt(CURRENT_PROCESS, -1) && remove_event_for_process(CURRENT_PROCESS, currTime))
                        {
                            add_event(new Event(TRANS_TO_READY, CURRENT_PROCESS, currTime));
                        }
                    }
                }
                break;
            }
            case TRANS_TO_RUN:
            {
                //base case of not having correct status but this shouldn't happen ideally
                //no clue why I have these errors set up
                //probably cause of Lab 1 PTSD
                if (proc->status != READY)
                {
                    error_handler();
                }

                //base case if it has no more cpu burst
                if (proc->cpuBurstRemaining == 0)
                {
                    int newCpuBurst = myrandom(proc->maxCpuBurst);
                    int newVal = min(newCpuBurst, proc->cpuTimeRemaining);
                    proc->cpuBurstRemaining = newVal;
                }

                // create event for either preemption or blocking
                // check preemption
                bool val = scheduler->test_preempt(proc, currTime);
                if (proc->cpuBurstRemaining <= quantum)
                {
                    //not pre empted
                    if (proc->cpuBurstRemaining >= proc->cpuTimeRemaining)
                    {
                        //event done at this point
                        add_event(new Event(TRANS_TO_FINISH, evt->evtProcess, currTime + proc->cpuTimeRemaining));
                    }
                    else
                    {
                        //after the CPU burst, is blocked for the IO call/burst
                        add_event(new Event(TRANS_TO_BLOCK, evt->evtProcess, currTime + proc->cpuBurstRemaining));
                    }
                }
                else
                {
                    //cases of preemption
                    //In case of RR, PREPRIO and PRIO it can be preempted by a quantum expiry
                    //preemption active here, is preempted after the quantum time in case of RR, PREPRIO
                    add_event(new Event(TRANS_TO_READY, evt->evtProcess, currTime + quantum));
                }
                //verbose print
                if (verbose)
                {
                    cout << currTime << " " << proc->processId << " " << timeInPrevState << ": READY -> RUNNING cb=" << proc->cpuBurstRemaining << " rem=" << proc->cpuTimeRemaining << " prio=" << proc->priority << endl;
                }
                //add to wait time for stats later
                proc->waitTime += timeInPrevState;
                //change status
                proc->status = RUNNING;
                break;
            }
            case TRANS_TO_BLOCK:
            {
                //create an event for when process becomes READY again
                int newIoBurst = myrandom(proc->maxIOBurst);
                proc->cpuTimeRemaining -= proc->cpuBurstRemaining;
                proc->cpuBurstRemaining = 0;
                proc->ioBurstRemaining = newIoBurst;

                //cpu occupied
                cpuOccupied += timeInPrevState;

                //io occupation too
                if (nextIOEndTime < currTime + newIoBurst)
                {
                    if (nextIOEndTime >= currTime)
                    {
                        //case of updated IO End Time , and parallel, only add the extra, not the overlap
                        ioOccupied += currTime + newIoBurst - nextIOEndTime;
                    }
                    else
                    {
                        //add the entire burst since the previous one finished before current time
                        ioOccupied += newIoBurst;
                    }
                    //update the IO End Time
                    nextIOEndTime = currTime + newIoBurst;
                }

                add_event(new Event(TRANS_TO_READY, evt->evtProcess, currTime + newIoBurst));

                //print verbose
                if (verbose)
                {
                    cout << currTime << " " << proc->processId << " " << timeInPrevState << ": RUNNING->BLOCKED ib=" << proc->ioBurstRemaining << " rem=" << proc->cpuTimeRemaining << endl;
                }

                //move to blocked
                proc->status = BLOCKED;
                CURRENT_PROCESS = nullptr;
                CALL_SCHEDULER = true;
                break;
            }
            case TRANS_TO_PREEMPT:
            {
                // add to runqueue (no event is generated)
                proc->status = BLOCKED;
                scheduler->add_process(proc);
                CALL_SCHEDULER = true;
                break;
            }
            case TRANS_TO_FINISH:
            {
                //print verbose
                if (verbose)
                {
                    cout << currTime << " " << proc->processId << " " << timeInPrevState << ": RUNNING -> DONE" << endl;
                }
                cpuOccupied += timeInPrevState;
                proc->finishingTime = currTime;
                proc->status = FINISHED;
                CURRENT_PROCESS = nullptr;
                CALL_SCHEDULER = true;
                break;
            }
            default:
            {
                //again should not come here ideally
                error_handler();
            }
            }
            proc->state_ts = currTime;
            lastEventFinish = currTime;
            // remove current event object from Memory
            delete evt;
            evt = nullptr;
            if (CALL_SCHEDULER)
            {
                if (get_next_event_time() == currTime)
                {
                    continue; //process next event from Event queue
                }
                CALL_SCHEDULER = false; // reset global flag
                if (CURRENT_PROCESS == nullptr)
                {
                    CURRENT_PROCESS = scheduler->get_next_process();
                    if (CURRENT_PROCESS == nullptr)
                    {
                        continue;
                    }
                    add_event(new Event(TRANS_TO_RUN, CURRENT_PROCESS, currTime));
                    // create event to make this process runnable for same time.
                }
            }
        }
    }
};

// reading file handler section

void readFileAndInit(EventSimulator *simulator, vector<Process *> &processList)
{
    ifstream inputFile;
    inputFile.open(inputFileName);
    if (!inputFile)
    {
        cerr << "File opening error";
        exit(1);
    }
    int processId = 0;
    string currLine;
    int AT, TC, CB, IO;
    while (inputFile >> AT >> TC >> CB >> IO)
    {
        // AT, TC, CB, IO
        //create process and add to list
        Process *p = new Process(processId, AT, TC, CB, IO, simulator->myrandom(maxPrios));
        processList.push_back(p);
        //add to simulator as event of creation and move to ready
        simulator->add_event(new Event(TRANS_TO_READY, p, AT));
        processId++;
    }
    inputFile.close();
}

void error_handler()
{
    cout << "Illegal usage of terms;" << endl;
    exit(1);
}

void print_result(vector<Process *> &processList,
                  EventSimulator *DES)
{
    //print thread message:
    int totalTT = 0;
    int totalCW = 0;
    cout << schedulerOptionPrint << endl;
    for (int id = 0; id < processList.size(); id++)
    {
        Process *ptrP = processList[id];
        totalTT += ptrP->finishingTime - ptrP->arrivalTime;
        totalCW += ptrP->waitTime;
        printf("%04d: %4d %4d %4d %4d %1d | %5d %5d %5d %5d\n",
               (int)id,
               ptrP->arrivalTime,
               ptrP->totalCpuTime,
               ptrP->maxCpuBurst,
               ptrP->maxIOBurst,
               ptrP->static_priority,
               ptrP->finishingTime,
               ptrP->finishingTime - ptrP->arrivalTime,
               ptrP->ioTime,
               ptrP->waitTime);
    }
    printf("SUM: %d %.2lf %.2lf %.2lf %.2lf %.3lf\n",
           DES->lastEventFinish,
           (double)(100.00) * DES->cpuOccupied / DES->lastEventFinish,
           (double)(100.00) * DES->ioOccupied / DES->lastEventFinish,
           (double)(1.00) * totalTT / processList.size(),
           (double)(1.00) * totalCW / processList.size(),
           (double)(100.00) * processList.size() / DES->lastEventFinish);
}

int main(int argc, char *argv[])
{
    int c;
    while ((c = getopt(argc, argv, "vs:")) != -1)
    {
        switch (c)
        {
        case 'v':
            verbose = true;
            break;
        case 's':
            schedulerType = string(optarg);
            break;
        default:
            error_handler();
        }
    }
    if (argc - 2 != optind)
    {
        //illegal message
        error_handler();
    }
    inputFileName = string(argv[optind]);
    randomFileName = string(argv[optind + 1]);
    IScheduler *scheduler;
    int quantum = MAX_QUANTUM_TS;

    switch (schedulerType.at(0))
    {
    case 'F':
        scheduler = new FCFSScheduler();
        schedulerOptionPrint = "FCFS";
        break;
    case 'L':
        scheduler = new LCFSScheduler();
        schedulerOptionPrint = "LCFS";
        break;
    case 'R':
        try
        {
            quantum = stoi(schedulerType.substr(1, schedulerType.size()));
            if (quantum <= 0)
            {
                error_handler();
            }
            schedulerOptionPrint = "RR " + to_string(quantum);
            scheduler = new RRScheduler(quantum);
        }
        catch (...)
        {
            //case of quantum not being a string
            error_handler();
        }
        break;
    case 'S':
        scheduler = new SRTFScheduler();
        schedulerOptionPrint = "SRTF";
        break;
    case 'P':
        try
        {
            int prios;
            //get priority if it is there
            if (schedulerType.find(":") != string::npos)
            {
                int pos = schedulerType.find(":");
                prios = stoi(schedulerType.substr(pos + 1, schedulerType.size()));
                schedulerType = schedulerType.substr(0, pos);
                maxPrios = prios;
            }
            else
            {
                prios = maxPrios;
            }
            quantum = stoi(schedulerType.substr(1, schedulerType.size()));
            if (quantum <= 0)
            {
                error_handler();
            }
            schedulerOptionPrint = "PRIO " + to_string(quantum);
            //send priority here
            scheduler = new PRIOScheduler(prios, quantum);
        }
        catch (...)
        {
            error_handler();
        }
        break;
    case 'E':
        try
        {
            int prios;
            //get priority if it is there
            if (schedulerType.find(":") != string::npos)
            {
                int pos = schedulerType.find(":");
                prios = stoi(schedulerType.substr(pos + 1, schedulerType.size()));
                schedulerType = schedulerType.substr(0, pos);
                maxPrios = prios;
            }
            else
            {
                prios = maxPrios;
            }
            quantum = stoi(schedulerType.substr(1, schedulerType.size()));
            if (quantum <= 0)
            {
                error_handler();
            }
            schedulerOptionPrint = "PREPRIO " + to_string(quantum);
            //send priority here
            scheduler = new PREPRIOScheduler(prios, quantum);
        }
        catch (...)
        {
            error_handler();
        }
        break;
    default:
        error_handler();
        break;
    }

    //start up the event simulator model
    EventSimulator *DES = new EventSimulator(scheduler, randomFileName, quantum);

    vector<Process *> processList;
    readFileAndInit(DES, processList);

    //start the simulation
    DES->Simulation();

    print_result(processList, DES);
}