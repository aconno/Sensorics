function playCounter() {

    var _canAnimate = (function() {
            // https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Animations/Detecting_CSS_animation_support
            var animation = false,
                animationstring = 'animation',
                keyframeprefix = '',
                domPrefixes = 'Webkit Moz O ms Khtml'.split(' '),
                pfx = '',
                elem = document.createElement('div');
            if ("ActiveXObject" in window) { return false; } // IE11- detected
            if (elem.style.animationName !== undefined && elem.style.transformStyle !== undefined) { animation = true; }
            if (animation === false) {
                for (var i = 0; i < domPrefixes.length; i++) {
                    if (elem.style[domPrefixes[i] + 'AnimationName'] !== undefined && elem.style[domPrefixes[i] + 'TransformStyle'] !== undefined) {
                        pfx = domPrefixes[i];
                        animationstring = pfx + 'Animation';
                        keyframeprefix = '-' + pfx.toLowerCase() + '-';
                        animation = true;
                        break;
                    }
                }
            }
            return animation;
        })(),
        _counterList = document.querySelectorAll('.animated-counter'),
        _i, _l, _counter, _count, _value, _valueAsArray, _valueAsPaddedArray, _j, _emptyArray, _width, _comingSoon, _lang,
        _innerAnimated, _numberAnimated, _separatorAnimated,
        _innerFallback, _numberFallback, _separatorFallback,
        _comingSoonText = {
            'en-US': 'Coming Soon',
            'es-US': 'Próximamente'
        },
        _numberMap = function(_item, _index) {
            return '<li class="animated-counter__face"><span class="animated-counter__decal">' + _index + '</span></li>';
        }; 
    for (_i = 0, _l = _counterList.length; _i < _l; _i++) {
        _width = 0;
        _counter = _counterList[_i];
        _count = parseInt(_counter.dataset.count, 10) || 7;
        _value = parseInt(_counter.dataset.value, 10) || 0;
        _valueAsArray = _value.toString().split('');
        _valueAsPaddedArray = Array.apply(null, Array(_count - _valueAsArray.length)).map(function() { return "0"; }).concat(_valueAsArray);
        if (_canAnimate) {
            _innerAnimated = document.createElement('div');
            _innerAnimated.className = 'animated-counter__inner';
            _counter.appendChild(_innerAnimated);
        }
        _innerFallback = document.createElement('div');
        _innerFallback.className = 'animated-counter__fallback';
        _counter.appendChild(_innerFallback);
        for (_j = _valueAsPaddedArray.length - 1; _j >= 0; _j--) {
            _emptyArray = Array.apply(null, Array(10));
            if (_canAnimate) {
                _numberAnimated = document.createElement('div');
                _numberAnimated.className = 'animated-counter__number';
                _numberAnimated.innerHTML += '<div class="animated-counter__axle"><ol class="animated-counter__dial animated-counter__value--' + _valueAsPaddedArray[_valueAsPaddedArray.length - 1 - _j] + '">' + _emptyArray.map(_numberMap).join('') + '</ol></div>';
                if (_j === _valueAsPaddedArray.length - 1 || _j % 3 === 2) {
                    _numberAnimated.classList.add('animated-counter__number-sequence--start');
                }
                if (_j % 3 === 0) {
                    _numberAnimated.classList.add('animated-counter__number-sequence--stop');
                }
                _innerAnimated.appendChild(_numberAnimated);
            }
            _numberFallback = document.createElement('div');
            _numberFallback.className = 'animated-counter__fallback__number';
            _numberFallback.innerHTML = _valueAsPaddedArray[_valueAsPaddedArray.length - 1 - _j];
            _innerFallback.appendChild(_numberFallback);
            _width += _numberFallback.clientWidth;
            if (_j % 3 === 0 && _j > 0) {
                if (_canAnimate) {
                    _separatorAnimated = document.createElement('div');
                    _separatorAnimated.className = 'animated-counter__comma';
                    _innerAnimated.appendChild(_separatorAnimated);
                }
                _separatorFallback = document.createElement('div');
                _separatorFallback.className = 'animated-counter__comma';
                _innerFallback.appendChild(_separatorFallback);
                _width += _separatorFallback.clientWidth;
            }
        }
        /*if (_value === 0) {
            _lang = document.getElementById('Wrapper').lang || 'en-US';
            _counter.classList.add('cant-stop-wont-stop');
            _comingSoon = document.createElement('div');
            _comingSoon.className = 'animated-counter__coming-soon';
            _comingSoon.innerHTML = '<div class="animated-counter__coming-soon__background"></div><div class="animated-counter__coming-soon__text">' + _comingSoonText[_lang] + '</div>';
            _counter.appendChild(_comingSoon);
        }*/
        _innerFallback.style.width = _width + 'px';
        _counter.style.width = _innerFallback.offsetWidth + 'px';
        if (_canAnimate) {
            _innerAnimated.style.width = _width + 'px';
            _innerFallback.classList.add('not-in-use');
        }
        _counter.classList.add('done');
        _counter.classList.add('play');
    }
}