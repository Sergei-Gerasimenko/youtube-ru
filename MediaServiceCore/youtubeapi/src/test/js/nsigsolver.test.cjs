const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');

function solve(input) {
    const context = vm.createContext({ input });
    for (const script of ['polyfill.js', 'meriyah-6.1.4.min.js', 'astring-1.9.0.min.js', 'yt.solver.core.js']) {
        vm.runInContext(fs.readFileSync(path.join(__dirname, '../../main/assets/nsigsolver', script), 'utf8'), context);
    }
    return JSON.parse(JSON.stringify(vm.runInContext('jsc(input)', context, { timeout: 5000 })));
}

// Synthetic player: the real TCL URL factory has the same masked set(alr, yes)
// call, while its startup-state array is empty in the extracted player.
function player(mask, obfuscated = true, inconsistent = false) {
    const marker = obfuscated
        ? `var selector = startup[7] % 90000;
           url[words[selector ^ ${mask}]](words[selector ^ ${mask ^ 1}], words[selector ^ ${mask ^ (inconsistent ? 3 : 2)}]);`
        : `url.set('alr', 'yes');`;
    return `(function () {
        'use strict';
        var words = 'set|alr|yes|no'.split('|');
        var startup = [];
        function Stream() { this.values = {}; }
        Stream.prototype.set = function (key, value) { this.values[key] = value; };
        Stream.prototype.get = function (key) { return this.values[key]; };
        Stream.prototype.decode = function () {
            var n = this.get('n');
            if (n) this.set('n', n.split('').reverse().join(''));
        };
        function factory(input, key, signature) {
            var url = new Stream(input);
            ${marker}
            if (signature) url.set(key, signature.split('').reverse().join(''));
            return url;
        }
    }).call(this);`;
}

const requests = [{ type: 'n', challenges: ['test-n'] }, { type: 'sig', challenges: ['abc123'] }];
const expected = [
    { type: 'result', data: { 'test-n': 'n-tset' } },
    { type: 'result', data: { abc123: '321cba' } },
];

test('restores a TCL URL factory without executing player startup code', () => {
    for (const mask of [42, 7122, 19351]) {
        assert.deepEqual(solve({ type: 'player', player: player(mask), requests }).responses, expected);
    }
});

test('keeps literal web URL factories working', () => {
    assert.deepEqual(solve({ type: 'player', player: player(0, false), requests }).responses, expected);
});

test('prepared TCL players can be reused for another challenge', () => {
    const result = solve({ type: 'player', player: player(42), requests, output_preprocessed: true });
    const cached = solve({ type: 'preprocessed', preprocessed_player: result.preprocessed_player,
        requests: [{ type: 'n', challenges: ['second'] }] });
    assert.deepEqual(cached.responses, [{ type: 'result', data: { second: 'dnoces' } }]);
});

test('does not guess masks when the set/alr/yes markers disagree', () => {
    const result = solve({ type: 'player', player: player(42, true, true), requests });
    assert.ok(result.responses.every(response => response.type === 'error'));
});
