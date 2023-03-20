

;functions for arg max function
(define (square x) (* x x))
(define (invert a) (/ 1000 a))

;Question 1
(define (arg-max f L)
  (let loop ((L (cdr L))
             (m (f (car L)))
             (k (car L)))
    (cond
      ((null? L) k)
      ((> ( f (car L)) m)
       (loop (cdr L) ( f (car L)) (car L)))
      (else
       (loop (cdr L) m k)))))


;Question 2
(define (zip . L)
  L)

;Question 3
(define (unzip L m)
  (cond
    ((null? L) '())
    ((= 0 m)
     (car L))
    (else
     (unzip (cdr L) (- m 1)))))


;helper function check elements in a input list
(define (check-list L m)
  (cond
    ((null? L) #f)
    ((= m (car L))
     #t)
    (else
     (check-list (cdr L) m))))

;Question 4
(define (intersectlist L1 L2)
  (let fnc ((FinList '())
            (L1 L1)
            (L2 L2))
    (cond
      ((null? L1) FinList)
      ((and (check-list L2 (car L1)) (not (check-list FinList (car L1))))
       (fnc (append FinList (list (car L1))) (cdr L1) L2))
      (else
       (fnc FinList (cdr L1) L2)))))

;Question 5
(define (sortedmerge L1 L2)
  (let fnc ((FinList '())
            (L1 L1)
            (L2 L2))
    (cond
      ((null? L1) (append FinList L2))
      ((null? L2) (append FinList L1))
      ((< (car L1) (car L2)) (fnc (append FinList (list (car L1))) (cdr L1) L2))
      (else (fnc (append FinList (list (car L2))) L1 (cdr L2))))))

;Question 6
(define (interleave L1 L2)
  (let fnc ((FinList '())
            (L1 L1)
            (L2 L2)
            (k 0))
    (cond
      ((null? L1) (append FinList L2))
      ((null? L2) (append FinList L1))
      ((= 0 k) (fnc (append FinList (list (car L1))) (cdr L1) L2 1))
      (else (fnc (append FinList (list (car L2))) L1 (cdr L2) 0)))))


;helper function inc
(define (inc x) (+ x 1))

;Question 7
(define (map2 L1 L2 pred f)
  (let fnc ((FinList '())
            (L1 L1)
            (L2 L2)
            (pred pred)
            (f f))
    (cond
      ((and (null? L1) (null? L2)) FinList)
      ((or (null? L1) (null? L2)) (string #\e #\r #\r #\o #\r))
      ((pred (car L1)) (fnc (append FinList (list (f (car L2)))) (cdr L1) (cdr L2) pred f))
      (else (fnc (append FinList (list (car L2))) (cdr L1) (cdr L2) pred f)))))



;helper function to check for element in a list
(define (is-present-list list element)
  (let loop ((L1 list))
    (cond
      ((null? L1) #f)
      ((equal? (car L1) element) #t)
      (else (loop (cdr L1))))))


;helper function for adjacent element list , along with the element X , as list(element list(adjacent elements))
(define (get-list-of-adjacent-elements edgeList element)
  (let loop ((adjacentList '())
             (L1 edgeList))
    (cond
      ((null? L1) adjacentList)
      ((equal? (caar L1) element) (loop (append adjacentList (cdar L1)) (cdr L1)))
      (else (loop adjacentList (cdr L1))))))

;Question 8 Part 1 (edge_list_to_adjacency_list)
(define (edge-list-to-adjacency-list edgeList)
  (let loop ((visited-elements '())
             (final-list '())
             (L1 edgeList))
    (cond
      ((null? L1) final-list)
      (else
       (cond
         ((and (is-present-list visited-elements (caar L1)) (is-present-list visited-elements (cadar L1))) (loop visited-elements final-list (cdr L1)))
         ((is-present-list visited-elements (caar L1)) (loop (append visited-elements (list (cadar L1))) (append final-list (list (list (cadar L1) (get-list-of-adjacent-elements edgeList (cadar L1))))) (cdr edgeList)))
         ((is-present-list visited-elements (cadar L1)) (loop (append visited-elements (list (caar L1))) (append final-list (list (list (caar L1) (get-list-of-adjacent-elements edgeList (caar L1))))) (cdr edgeList)))
         (else (loop (append visited-elements (list (caar L1) (cadar L1))) (append final-list (list (list (caar L1) (get-list-of-adjacent-elements edgeList (caar L1)))) (list (list (cadar L1) (get-list-of-adjacent-elements edgeList (cadar L1))))) (cdr edgeList)))))
    )))


;helper function to create edge list from the initial adjacency list for all elements
(define (create-edge-list initialList)
  (let loop ((element (caar initialList))
             (list-lvl (cadr initialList))
             (final-list '()))
    (cond
      ((null? list-lvl) final-list)
      (else (loop element (cdr list-lvl) (append final-list (list (list element (car list-lvl)))))))))


;adjacency-list-to-edge-list Question 8 Part 2
(define (adjacency-list-to-edge-list adjacencyList)
  (let loop ((final-list '())
             (L1 adjacencyList))
    (cond
      ((null? L1) final-list)
      (else
       (cond
         ((null? (cdar L1)) (loop final-list (cdr L1)))
         (else (loop (append final-list (create-edge-list (car L1))) (cdr L1))))))))