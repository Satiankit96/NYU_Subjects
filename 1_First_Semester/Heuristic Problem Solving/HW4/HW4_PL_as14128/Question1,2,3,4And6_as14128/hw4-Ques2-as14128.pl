/*Question 2 solutions*/


/*Part 1 : remove_items*/
remove_items(_,[],[]).
remove_items(L,[X|Tail],Result) :- member(X,L),remove_items(L,Tail,Result),!.
remove_items(L,[X|Tail],[X|Result]) :- remove_items(L,Tail,Result).

/*Part 2 : Intersection2*/
set([],[]).
set([X|Tail],Set):- member(X,Tail),!,set(Tail,Set).
set([X|Tail],[X|Set]) :- set(Tail,Set).
intersection2(A,B,X) :- intersection(A,B,S),set(S,X).

/*Part 3 : disjunct_union*/
disjunct_one(_,[],[]).
disjunct_one(L1,[X|L2],U) :- member(X,L1),!,disjunct_one(L1,L2,U).
disjunct_one(L1,[X|L2],U) :- member(X,L1),!,disjunct_one(L1,L2,U).
disjunct_one(L1,[X|L2],[X|U]) :- disjunct_one(L1,L2,U).
disjunct_union(L1,L2,U) :- disjunct_one(L1,L2,X) , disjunct_one(L2,L1,Y), union(X,Y,U).

/*Part 4 : my_flatten*/
my_flatten([], []) :- !.
my_flatten([X|Tail], Result) :- !,flatten2(X, NewX),flatten2(Tail, NewTail),append(NewX, NewTail, Result).
flatten2(L, [L]).


/*Part 5 : compress*/
compress([],[]).
compress([X],[X]).
compress([X,X|Tail], [X|Result]) :- compress([X|Tail],[X|Result]).
compress([X,Y|Tail], [X|Result]) :- X \== Y,compress([Y|Tail],Result).

/*Part 6 : encode*/
encode([],[]).
encode([X|T],[[C1,X]|R]) :- encode(T,[[C,X]|R]), !, C1 is C+1.
encode([X|T],[[1,X]|R]) :- encode(T,R).

/*Part 7 : encode_modified*/
% Q7
% Example:
% ?- encode_modified([a,a,a,a,b,c,c,a,a,d,e,e,e,e],X).
% X = [[4,a],b,[2,c],[2,a],d,[4,e]]

do_modification([], []) :- !.
do_modification([[1,X]|L], O) :- do_modification(L, O1), append([X], O1, O), !.
do_modification([A|L], O) :- do_modification(L, O1), append([A], O1, O), !.

encode_modified(L, O) :- encode(L, L1), do_modification(L1, O).



/*Part 8 : rotate*/

rotleft([E|T], R) :- append(T,[E],R).
rotright(L, R) :- rotleft(R, L).

rotate(L, 0, L) :- !.
rotate(L1, N, L2) :-
  N < 0, rotright(L1, L), N1 is N+1, rotate(L, N1, L2).
rotate(L1, N, L2) :-
  N > 0, rotleft(L1, L), N1 is N-1, rotate(L, N1, L2).

        