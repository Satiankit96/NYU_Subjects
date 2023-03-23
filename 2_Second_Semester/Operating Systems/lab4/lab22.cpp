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
#include <climits>
#include <bitset>
#include <memory>
#include <unistd.h>

#define ull unsigned long long
#define ui unsigned int

using namespace std;

enum class Movement
{
    Up,
    Down
};

//can move Up or Down
ui totalTracks = 0;
int currentTrack = 0;
Movement movement = Movement::Up;

struct IORequest
{
    ui blockNum;
    ui reqNum;
    ui completionTime;
    ui arrTime;
    ui issueTime;

    explicit IORequest(ui blockNum, ui reqNum, ui arrTime)
    {
        this->blockNum = blockNum;
        this->reqNum = reqNum;
        this->completionTime = 0;
        this->arrTime = arrTime;
        this->issueTime = 0;
    }
};

shared_ptr<IORequest> currentRequest = nullptr;

//Utility Function
void removeFromListReqNum(vector<shared_ptr<IORequest>> &lst, int reqNum)
{
    lst.erase(
        remove_if(lst.begin(), lst.end(),
                  [&](const shared_ptr<IORequest> &req) -> bool
                  { return reqNum == req->reqNum; }),
        lst.end());
    return;
}

class SchedulingStrategyBase
{
public:
    virtual bool isEmpty() = 0;
    virtual void addData(shared_ptr<IORequest> request) = 0;
    virtual shared_ptr<IORequest> getNextStrategy() = 0;
};

class FIFO : public SchedulingStrategyBase
{
    vector<shared_ptr<IORequest>> vec;

public:
    bool isEmpty()
    {
        return vec.empty();
    }

    void addData(shared_ptr<IORequest> request)
    {
        this->vec.push_back(request);
    }

    shared_ptr<IORequest> getNextStrategy()
    {
        if (vec.empty())
        {
            return nullptr;
        }
        shared_ptr<IORequest> result = vec.front();
        vec.erase(vec.begin());
        return result;
    }
};

class SSTF : public SchedulingStrategyBase
{
    vector<shared_ptr<IORequest>> vec;

public:
    bool isEmpty()
    {
        return vec.empty();
    }

    void addData(shared_ptr<IORequest> request)
    {
        this->vec.push_back(request);
    }

    shared_ptr<IORequest> getNextStrategy()
    {
        if (isEmpty())
        {
            return nullptr;
        }

        int closest = INT_MAX;
        int index;
        shared_ptr<IORequest> result = nullptr;

        for (int i = 0; i < vec.size(); i++)
        {
            int block = vec[i]->blockNum;
            //check if it it is nearest or not
            if (closest > abs(block - currentTrack))
            {
                result = vec[i];
                closest = abs(block - currentTrack);
                //set for erase
                index = i;
            }
        }
        //remove the request from list
        vec.erase(vec.begin() + index);
        return result;
    }
};

class LOOK : public SchedulingStrategyBase
{
public:
    vector<shared_ptr<IORequest>> vec;
    bool isEmpty()
    {
        return vec.empty();
    }

    void addData(shared_ptr<IORequest> request)
    {
        this->vec.push_back(request);
    }

    shared_ptr<IORequest> getNextStrategy()
    {
        if (isEmpty())
        {
            return nullptr;
        }
        vector<shared_ptr<IORequest>> reqInSameDirection;
        vector<shared_ptr<IORequest>> reqInOppositeDirection;

        for (auto request : vec)
        {
            if ((request->blockNum >= currentTrack && movement == Movement::Up) || (request->blockNum <= currentTrack && movement == Movement::Down))
            {
                reqInSameDirection.push_back(request);
            }
            else
            {
                reqInOppositeDirection.push_back(request);
            }
        }

        if (reqInSameDirection.empty())
        {
            if (movement == Movement::Up)
            {
                movement = Movement::Down;
            }
            else
            {
                movement = Movement::Up;
            }
            swap(reqInSameDirection, reqInOppositeDirection);
        }

        shared_ptr<IORequest> nearestReq = nullptr;
        int nearestReqNum = -1;
        int mnVal = INT_MAX;
        for (int i = 0; i < reqInSameDirection.size(); i++)
        {
            int block = reqInSameDirection[i]->blockNum;
            if (mnVal > abs(block - currentTrack))
            {
                nearestReq = reqInSameDirection[i];
                mnVal = abs(block - currentTrack);
                nearestReqNum = reqInSameDirection[i]->reqNum;
            }
        }
        vec.erase(
            remove_if(vec.begin(), vec.end(),
                      [&](const shared_ptr<IORequest> &req) -> bool
                      { return nearestReqNum == req->reqNum; }),
            vec.end());
        return nearestReq;
    }
};

class CLOOK : public SchedulingStrategyBase
{
    vector<shared_ptr<IORequest>> vec;

public:
    bool isEmpty()
    {
        return vec.empty();
    }

    void addData(shared_ptr<IORequest> req)
    {
        this->vec.push_back(req);
    }

    shared_ptr<IORequest> getNextStrategy()
    {
        if (vec.empty())
        {
            return nullptr;
        }
        vector<shared_ptr<IORequest>> reqInSameDirection;
        vector<shared_ptr<IORequest>> reqInOppDirection;
        for (auto request : vec)
        {
            if (request->blockNum >= currentTrack)
            {
                reqInSameDirection.push_back(request);
            }
            else
            {
                reqInOppDirection.push_back(request);
            }
        }
        if (reqInSameDirection.size() == 0)
        {
            int mnVal = INT_MAX;
            int nearestReqNum = -1;
            shared_ptr<IORequest> nearestReq = nullptr;

            for (int i = 0; i < reqInOppDirection.size(); i++)
            {
                int currTrack = reqInOppDirection[i]->blockNum;
                if (mnVal > currTrack)
                {
                    nearestReq = reqInOppDirection[i];
                    nearestReqNum = reqInOppDirection[i]->reqNum;
                    mnVal = currTrack;
                }
            }
            for (int i = 0; i < vec.size(); i++)
            {
                if (vec[i]->reqNum == nearestReqNum)
                {
                    vec.erase(vec.begin() + i);
                    break;
                }
            }
            return nearestReq;
        }
        int mnVal = INT_MAX;
        int nearestReqNum = -1;
        shared_ptr<IORequest> nearestReq = nullptr;
        for (int i = 0; i < reqInSameDirection.size(); i++)
        {
            int block = reqInSameDirection[i]->blockNum;
            if (mnVal > abs(block - currentTrack))
            {
                mnVal = abs(block - currentTrack);
                nearestReqNum = reqInSameDirection[i]->reqNum;
                nearestReq = reqInSameDirection[i];
            }
        }
        for (int i = 0; i < vec.size(); i++)
        {
            if (vec[i]->reqNum == nearestReqNum)
            {
                vec.erase(vec.begin() + i);
                break;
            }
        }
        return nearestReq;
    }
};

class FLOOK : public SchedulingStrategyBase
{
    vector<shared_ptr<IORequest>> activeQueue;
    vector<shared_ptr<IORequest>> addQueue;

public:
    bool isEmpty()
    {
        return activeQueue.empty() && addQueue.empty();
    }
    void addData(shared_ptr<IORequest> request)
    {
        addQueue.push_back(request);
    }

    shared_ptr<IORequest> getNextStrategy()
    {
        if (activeQueue.empty() && addQueue.size() == 0)
        {
            return nullptr;
        }

        if (activeQueue.empty())
        {
            swap(activeQueue, addQueue);
        }

        vector<shared_ptr<IORequest>> reqInSameDirection;
        vector<shared_ptr<IORequest>> reqInOppDirection;

        for (int i = 0; i < activeQueue.size(); i++)
        {
            auto request = activeQueue[i];
            if ((request->blockNum >= currentTrack && movement == Movement::Up) || (request->blockNum <= currentTrack && movement == Movement::Down))
            {
                reqInSameDirection.push_back(request);
            } else {
                reqInOppDirection.push_back(request);
            }
        }

        if (reqInSameDirection.size() == 0)
        {
            if (movement == Movement::Up)
            {
                movement = Movement::Down;
            }
            else
            {
                movement = Movement::Up;
            }
            swap(reqInSameDirection, reqInOppDirection);
        }

        if (reqInSameDirection.size() == 0)
        {
            return nullptr;
        }

        int mnValue = INT_MAX;
        int closestReqNbr = -1;
        shared_ptr<IORequest> closestReq = nullptr;

        for (int i = 0; i < reqInSameDirection.size(); i++)
        {
            int block = reqInSameDirection[i]->blockNum;
            if (mnValue > abs(block - currentTrack))
            {
                closestReq = reqInSameDirection[i];
                mnValue = abs(block - currentTrack);
                closestReqNbr = reqInSameDirection[i]->reqNum;
            }
        }
        for (int i = 0; i < activeQueue.size(); i++)
        {
            if (activeQueue[i]->reqNum == closestReqNbr)
            {
                activeQueue.erase(activeQueue.begin() + i);
                break;
            }
        }
        return closestReq;
    }
};

class Reader
{
public:
    string inputFile;
    int currRequestNum;

    Reader(string _inputFile)
    {
        inputFile = _inputFile;
        currRequestNum = 0;
    }

    vector<shared_ptr<IORequest>> read()
    {
        vector<shared_ptr<IORequest>> results;
        ifstream file(inputFile.c_str());
        string currLine;
        while (getline(file, currLine))
        {
            if (currLine[0] == '#')
            {
                continue;
            }

            stringstream lineStream(currLine);
            ui arrTime, reqBlockNum;
            lineStream >> arrTime >> reqBlockNum;
            //ui blockNum, ui reqNum, ui arrTime
            results.push_back(make_shared<IORequest>(reqBlockNum, currRequestNum, arrTime));
            currRequestNum++;
        }
        return results;
    }
};

class IOScheduler
{
public:
    shared_ptr<SchedulingStrategyBase> strategy;
    bool isIOPending;

    IOScheduler(shared_ptr<SchedulingStrategyBase> _strategy)
    {
        strategy = _strategy;
        isIOPending = true;
    }

    bool empty()
    {
        return strategy->isEmpty();
    }

    void addData(shared_ptr<IORequest> request)
    {
        strategy->addData(request);
    }

    shared_ptr<IORequest> getNext()
    {
        return strategy->getNextStrategy();
    }
};

class Simulator
{
public:
    ui time = 0u;
    string inputFile;
    shared_ptr<IOScheduler> ioScheduler;
    bool logv = false, logf = false, logq = false;

    Simulator(char strategyName, string _inputFile, bool _logv, bool _logf, bool _logq)
    {
        inputFile = _inputFile;
        shared_ptr<SchedulingStrategyBase> strategy;
        switch (strategyName)
        {
        case 'i':
            strategy = make_shared<FIFO>();
            break;
        case 'j':
            strategy = make_shared<SSTF>();
            break;
        case 's':
            strategy = make_shared<LOOK>();
            break;
        case 'c':
            strategy = make_shared<CLOOK>();
            break;
        case 'f':
            strategy = make_shared<FLOOK>();
            break;
        default:
            cerr << "Incorrect strategy" << endl;
            exit(1);
        }
        ioScheduler = make_shared<IOScheduler>(strategy);
        logv = _logv;
        logf = _logf;
        logq = _logq;
    }

    void simulate()
    {
        //init reader here
        Reader reader(inputFile);
        auto requestList = reader.read();
        auto copyList = requestList;
        bool isIOPending = true;
        while (isIOPending)
        {
            //if a new IO has come, add to the strategy queue
            if (requestList.size() != 0 && requestList.front()->arrTime == time)
            {
                auto request = requestList.front();
                requestList.erase(requestList.begin());
                ioScheduler->addData(request);
            }

            //check if any IO has been completed at this time, if yes, add to the final summary statistics
            if (currentRequest != nullptr && currentRequest->blockNum == currentTrack)
            {
                currentRequest->completionTime = time;
                //set to null pointer as current is completed
                currentRequest = nullptr;
            }
            //check if IO is active and isn't completed, in that case, seek and move currTrack in movement by one point
            if (currentRequest != nullptr)
            {
                if (movement == Movement::Up)
                {
                    currentTrack++;
                }
                else
                {
                    currentTrack--;
                }
                totalTracks++;
            }

            //if no IO request is currently active and there are pending IO tasks left
            //add a new task to the disk tracker
            if (currentRequest == nullptr && !ioScheduler->empty())
            {
                currentRequest = ioScheduler->getNext();
                while (currentRequest!=nullptr && currentRequest->blockNum == currentTrack)
                {
                    currentRequest->issueTime = time;
                    currentRequest->completionTime = time;
                    currentRequest = ioScheduler->getNext();
                }

                if (currentRequest != nullptr)
                {
                    if (currentTrack < currentRequest->blockNum)
                    {
                        movement = Movement::Up;
                        if (movement == Movement::Up)
                        {
                            currentTrack++;
                        }
                        else
                        {
                            currentTrack--;
                        }
                        totalTracks++;
                    }
                    else if (currentTrack > currentRequest->blockNum)
                    {
                        movement = Movement::Down;
                        if (movement == Movement::Up)
                        {
                            currentTrack++;
                        }
                        else
                        {
                            currentTrack--;
                        }
                        totalTracks++;
                    }
                    //we have to make up the time of issue, it is dealt with by the conditions above
                    currentRequest->issueTime = time;
                }
            }
            //check the case if there is no current request, no pending requests and no future requests
            //base case, end of simulation
            if (currentRequest == nullptr && ioScheduler->empty() && requestList.size() == 0)
            {
                isIOPending = false;
            }
            else
            {
                //increament the time as suggested by the Prof rather than DES
                time++;
            }
        }
        printSummary(copyList);
    }

    void printSummary(vector<shared_ptr<IORequest>> requestList)
    {
        ui maxWaitTime = 0;
        double avgTurnTime = 0;
        double avgWaitTime = 0;
        for (int i = 0; i < requestList.size(); i++)
        {
            avgWaitTime += requestList[i]->issueTime - requestList[i]->arrTime;
            avgTurnTime += requestList[i]->completionTime - requestList[i]->arrTime;
            if (maxWaitTime < requestList[i]->issueTime - requestList[i]->arrTime)
            {
                maxWaitTime = requestList[i]->issueTime - requestList[i]->arrTime;
            }
            //print for each request
            printf("%5lu: %5u %5u %5u\n", i, requestList[i]->arrTime, requestList[i]->issueTime, requestList[i]->completionTime);
        }
        avgWaitTime = avgWaitTime / requestList.size();
        avgTurnTime = avgTurnTime / requestList.size();
        //print final statistic
        printf("SUM: %d %d %.2lf %.2lf %d\n", time, totalTracks, avgTurnTime, avgWaitTime, maxWaitTime);
    }
};

int main(int argc, char **argv)
{
    bool logv, logf, logq;
    char algorithm = 'i';
    string algoOption;
    int option;
    while ((option = getopt(argc, argv, "s:vfq")) != -1)
    {
        switch (option)
        {
        case 's':
        {
            algoOption = optarg;
        }
        break;
        case 'v':
            logv = true;
            break;
        case 'q':
            logq = true;
            break;
        case 'f':
            logf = true;
            break;
        default:
            printf("Something's wrong!");
            abort();
        }
    }
    string inputFileName(argv[optind]);
    algorithm = algoOption[0];
    Simulator simulator(algorithm, inputFileName, logv, logf, logq);
    simulator.simulate();
}