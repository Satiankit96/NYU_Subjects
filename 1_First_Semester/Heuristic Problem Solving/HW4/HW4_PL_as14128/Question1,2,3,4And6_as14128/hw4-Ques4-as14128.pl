/*Question 4*/
team([tokyo, rio, berlin, denver]).

time(tokyo, 5).
time(rio, 10).
time(berlin, 20).
time(denver, 25).

cost([X], C) :- time(X, C).
cost([X|Tail], C) :- cost(Tail, C1), time(X, C2), C is max(C1,C2).


split(L, [X,Y], M) :- member(X,L), member(Y,L) , compare(<,X,Y), subtract(L,[X,Y], M).

nonMember(X, L) :- time(X,_), \+ member(X, L).


move(st(l, L1), st(r, L2), r(M), C) :- split(L1, M, L2), cost(M, C).
move(st(r, L1), st(l, L2), l(M), C) :- nonMember(M, L1), append(L1, [M], L2), time(M, C).




/*right_2, left_1, right_2, left_1, right_2*/
trans(I, F, M, C) :- move(I, St2, M1, C1), move(St2, St3, M2, C2), 
move(St3, St4, M3, C3),  move(St4, St5, M4, C4), 
move(St5, F, M5, C5), 
append([M1], [M2], Mt1), append(Mt1, [M3], Mt2), append(Mt2, [M4], Mt3),append(Mt3, [M5], M), 
C is C1+C2+C3+C4+C5.


cross(M,D) :- team(T), trans(st(l,T),st(r,[]),M,D0), D0=<D.

solution(M) :- cross(M,60).