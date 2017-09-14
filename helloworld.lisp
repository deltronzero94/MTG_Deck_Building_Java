; Hello World LISP Program
(print "Hello World")


(defun squarelist (xs)
  (loop for x in xs
     for i from 1
     collect (* x x)))

(print (squarelist '(1 2 3 4)))

(defun square-list (L)
    (if null L) '()
      (cons (* (car L) (car L) ) 
        (square-list (cdr L))
      )
)

(print (squarelist '(1 2 3 4 5)))


;; ************************************
;; Dot-Product Function (Problem # 6)
;; ************************************
(defun dot-product (a b)
    (if (or (null a) (null b)) 0 ;Base case
    (+ (* (first a) (first b)) 
        (dot-product (rest a) (rest b))))
)

(print (dot-product '(1.2 2.0 -0.2) '(0.0 2.3 5.0) ))

;; **************************************
;; Count-Number Function (Problem #7)
;; **************************************
(defun count-number(list)
   (cond  ((null list) 0) ;if list is empty / base case
            ((numberp (first list)) (+ 1 (count-number (rest list)))) ;if a number, add 1 and call again
            ((count-number (rest list))  (count-number (rest list))) ;if not a number, call again

   )
)

(print (count-number '(A 2.3 B 5 4.53 C F)))

;; ****************************************
;; New-List Function (Promblem #8)
;; ****************************************
(defun new-list(number)
    (cond ((or (null number) (eq number '0)) '()) ;List is null
            ((eq number '1) (list T))  ;Base Case
                ((append (list T) (new-list (decf number)))) ;Subtracts 1 and calls itself
    )
)

(print (new-list 5))

;; ***************************************
;; Length Function (Problem #9)
;; ***************************************
(defun all-length(l)
  (cond
    ((atom l) 1) ;If single element
    ((null l) 0) ;base case
    (t (+ (all-length (car l)) ;First Element +
        (all-length (cdr l)))  ;The rest of the elements
    )
  )
)

;(print 
;    (all-length '(a b ((a) c) e))
;)

;(print
;    (all-length '(A (B C D E) D (E F)))
;)

;(print
;    (all-length '(NIL NIL (NIL NIL) NIL))
;)

(defun count-atoms(l)
    (cond
        ((null l) 0)  ;base 
        ((atom l) 1) 
        (t (+ (count-atoms (car l)) (count-atoms (cdr l)) ))
    )
)

(defun len (list)
    (if list
    (1+ (len (cdr list) ))
    0
    )
)

(defun len1 (l)
    ;nothing more to check - return nil - no inner lists
    (if (null l)
    0
        ;the first element of the list is a list?
        (if (list (car l))
        ;if yes - return true
        (+ 1 (len1 (car l)) (len1 (cdr l)))
        ;otherwise - try for the cdr of the list
            ( t (+ (len1-p (car l))(len1-p (cdr l))))
        )
        
    )
)


(print
    (count-atoms '(A Z (B C D) E F))
)

(print
    (len1 '(NIL NIL (NIL NIL) NIL))
    ;(car (car '((NIL NIL) NIl NIL (NIL NIL))))
)

(print
    (if (list '(NIL)) T
        (F)
    )
)


