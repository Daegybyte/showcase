_let pair = _fun (a) _fun (b)
              _fun(sel)
              _if sel _then a _else b
_in _let fst = _fun (p) p(_true)
_in _let snd = _fun (p) p(_false)
_in _let fib = _fun (fib)
                _fun (x)
                  _if x == 0
                  _then pair(1)(1)
                  _else _if x == 1
                        _then pair(1)(1)
                        _else _let p = fib(fib)(x + -1)
                              _in pair(fst(p) + snd(p))(fst(p))
_in  fst(fib(fib)(30))
