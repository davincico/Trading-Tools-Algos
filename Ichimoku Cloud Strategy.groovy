// This source code is subject to the terms of the Mozilla Public License 2.0 at https://mozilla.org/MPL/2.0/
// © RickSimpson

//@version=5
indicator('Ichimoku Cloud', 'Ichimoku Cloud', overlay=true, max_bars_back=500, max_lines_count=500, max_labels_count=500)

//-----------------------------------------
//  Ichimoku Cloud Strategy
// ----------------------------------------

//◀─── Groups Setup ───►

gsrc     = 'SOURCE SETUP'
gichi    = 'ICHIMOKU SETTINGS'
gsr      = 'SUPPORTS/RESISTANCES SETTINGS'
gpsylvl  = 'PSYCHOLOGICAL LEVELS'
gadvset  = 'ADVANCED SETTINGS'
gvosc    = 'VOLUME OSCILLATOR SETTINGS'
gvrindex = 'RELATIVE VOLUME STRENGTH INDEX SETTINGS'
gatr     = 'VOLATILITY SETTINGS'
gbarcol  = 'BAR COLOR SETTINGS'
gtable   = 'PANEL SETTINGS'

//◀─── Constant String Declaration ───►

string obvstr = 'On Balance Volume'
string cvdstr = 'Cumulative Volume Delta'
string pvtstr = 'Price Volume Trend'

//◀─── Inputs ───►

i_src           = input.source(close, 'Source',                                                                                  group=gsrc)
i_altsrc        = input.string('(close > open ? high : low)', 'Alternative Source',                        options=['Default Source', '(open + close +3 * (high + low)) / 8', 'close + high + low -2 * open', '(close +5 * (high + low) -7 * (open)) / 4', '(open + close +5 * (high + low)) / 12', '(close > open ? high : low)', 'Heiken-Ashi'], group=gsrc)
i_uha           = input(true,                                 'Use Volume Heikin Ashi?',                                         group=gsrc)
i_wmas          = input.bool(true,                            'Weighted Moving Average Smoothing?',                              group=gsrc)
i_showlabels    = input.bool(true,                            'Show Labels?',                                                    group=gichi)
i_showprice     = input.bool(true,                            'Show Tenkan-Sen/kijun-Sen Price Labels?',                         group=gichi)
i_showtenkan    = input.bool(true,                            'Tenkan-Sen',                                                      group=gichi, inline='tenkan')
i_tenkancolor   = input.color(color.new(#007FFF, 0),          '',                                                                group=gichi, inline='tenkan')
i_tkminlen      = input.int(9,                                'Tenkan Min Length',                         minval=1,             group=gichi, inline='tenkanlength')
i_tkmaxlen      = input.int(30,                               'Tenkan Max Length',                         minval=1,             group=gichi, inline='tenkanlength')
i_tkdynperc     = input.float(96.85,                          'Tenkan Dynamic Length Adaptive Percentage', minval=0, maxval=100, group=gichi) / 100.0
i_tkvolsetup    = input.bool(true,                            'Volume',                                                          group=gichi, inline='tenkanfilter')
i_tkatrsetup    = input.bool(true,                            'Volatility',                                                      group=gichi, inline='tenkanfilter')
i_tkchfilter    = input.bool(true,                            'Chikou Trend Filter',                                             group=gichi, inline='tenkanfilter')
i_showkijun     = input.bool(true,                            'Kijun-Sen    ',                                                   group=gichi, inline='kijun')
i_kijuncolor    = input.color(color.new(#FF0016, 0),          '',                                                                group=gichi, inline='kijun')
i_showkjhlabels = input.bool(true,                            'Show Hidden Kijun-Sen Labels?',                                   group=gichi)
i_showkjhsr     = input.bool(true,                            'Show Hidden Kijun-Sen Supports/Resistances?',                     group=gichi)
i_kjminlen      = input.int(20,                               'Kijun Min Length',                          minval=1,             group=gichi, inline='kijunlength')
i_kjmaxlen      = input.int(60,                               'Kijun Max Length',                          minval=1,             group=gichi, inline='kijunlength')
i_kjdynperc     = input.float(96.85,                          'Kijun Dynamic Length Adaptive Percentage',  minval=0, maxval=100, group=gichi) / 100.0
i_kjdivider     = input.int(1,                                'Kijun Divider Tool',                        minval=1, maxval=4,   group=gichi)
i_kjvolsetup    = input.bool(true,                            'Volume',                                                          group=gichi, inline='kijunfilter')
i_kjatrsetup    = input.bool(true,                            'Volatility',                                                      group=gichi, inline='kijunfilter')
i_kjchfilter    = input.bool(true,                            'Chikou Trend Filter',                                             group=gichi, inline='kijunfilter')
i_showchikou    = input.bool(true,                            'Chikou Span',                                                     group=gichi)
i_chbearcol     = input.color(color.new(#FF0016, 0),          'Bear',                                                            group=gichi, inline='chikoucolor')
i_chbullcol     = input.color(color.new(#459915, 0),          'Bull',                                                            group=gichi, inline='chikoucolor')
i_chconsocol    = input.color(color.new(#FF9800, 0),          'Consolidation',                                                   group=gichi, inline='chikoucolor')
i_chminlen      = input.int(26,                               'Chikou Min Length',                         minval=1,             group=gichi, inline='chikoulength')
i_chmaxlen      = input.int(50,                               'Chikou Max Length',                         minval=1,             group=gichi, inline='chikoulength')
i_chdynperc     = input.float(96.85,                          'Chikou Dynamic Length Adaptive Percentage', minval=0, maxval=100, group=gichi) / 100.0
i_chfiltper     = input.int(25,                               'Chikou Filter Period',                      minval=1,             group=gichi)
i_chlb          = input.int(50,                               'Chikou Filter Percentage Lookback',         minval=1, maxval=89,  group=gichi)
i_chvolsetup    = input.bool(true,                            'Volume',                                                          group=gichi, inline='chikoufilter')
i_chatrsetup    = input.bool(true,                            'Volatility',                                                      group=gichi, inline='chikoufilter')
i_chtfilter     = input.bool(true,                            'Chikou Trend Filter',                                             group=gichi, inline='chikoufilter')
I_ska           = input.bool(true,                            'Senkou-Span A',                                                   group=gichi, inline='senkou')
I_skb           = input.bool(true,                            'Senkou-Span B',                                                   group=gichi, inline='senkou')
I_skbear        = input.color(color.new(#FF0016, 0),          'Bear',                                                            group=gichi, inline='senkoucolor')
I_skbull        = input.color(color.new(#459915, 0),          'Bull',                                                            group=gichi, inline='senkoucolor')
I_skconso       = input.color(color.new(#CED7DF, 0),          'Consolidation',                                                   group=gichi, inline='senkoucolor')
I_skminlen      = input.int(50,                               'Senkou-Span Min Length',                    minval=1,             group=gichi, inline='senkoulength')
I_skmaxlen      = input.int(120,                              'Senkou-Span Max Length',                    minval=1,             group=gichi, inline='senkoulength')
I_skperc        = input.float(96.85,                          'Senkou Dynamic Length Adaptive Percentage', minval=0, maxval=100, group=gichi) / 100.0
I_skoffset      = input.int(26,                               'Senkou-Span Offset',                        minval=1,             group=gichi)
I_skvolsetup    = input.bool(true,                            'Volume',                                                          group=gichi, inline='sn_chks')
I_atrsetup      = input.bool(true,                            'Volatility',                                                      group=gichi, inline='sn_chks')
I_skchfilter    = input.bool(true,                            'Chikou Trend Filter',                                             group=gichi, inline='sn_chks')
I_kumofill      = input.bool(true,                            'Kumo Fill',                                                       group=gichi, inline='kumo')
I_kumofillt     = input.int(65,                               'Transparency',                              minval=1,             group=gichi, inline='kumo')
I_volsetup      = input.bool(true,                            'Volume?',                                                         group=gadvset)
I_tkkjcross     = input.bool(true,                            'Tenkan-Sen/Kijun-Sen Cross?',                                     group=gadvset)
I_atrvolatility = input.bool(true,                            'Volatility?',                                                     group=gadvset)
I_tkeqkj        = input.bool(true,                            'Tenkan-Sen Equal Kijun-Sen?',                                     group=gadvset)
I_chgtp         = input.bool(true,                            'Chikou Greater Than Price?',                                      group=gadvset)
I_chmom         = input.bool(true,                            'Chikou Momentum?',                                                group=gadvset)
I_pgtk          = input.bool(true,                            'Price Greater Than Kumo?',                                        group=gadvset)
I_pgttk         = input.bool(true,                            'Price Greater Than Tenkan-Sen?',                                  group=gadvset)
I_pgtchf        = input.bool(true,                            'Chikou Trend Filter?',                                            group=gadvset)
I_volosctype    = input.string('On Balance Volume',           'Volume Oscillator Type',                    options=['TFS Volume Oscillator', 'On Balance Volume', 'Klinger Volume Oscillator', 'Cumulative Volume Oscillator', 'Volume Zone Oscillator'], group=gvosc)
I_volosctypetk  = input.string('On Balance Volume',           'Volume Oscillator Type for Tenkan-Sen',     options=['TFS Volume Oscillator', 'On Balance Volume', 'Klinger Volume Oscillator', 'Cumulative Volume Oscillator', 'Volume Zone Oscillator'], group=gvosc)
I_volosctypekj  = input.string('On Balance Volume',           'Volume Oscillator Type for Kijun-Sen',      options=['TFS Volume Oscillator', 'On Balance Volume', 'Klinger Volume Oscillator', 'Cumulative Volume Oscillator', 'Volume Zone Oscillator'], group=gvosc)
I_volosctypesk  = input.string('On Balance Volume',           'Volume Oscillator Type for Senkou',         options=['TFS Volume Oscillator', 'On Balance Volume', 'Klinger Volume Oscillator', 'Cumulative Volume Oscillator', 'Volume Zone Oscillator'], group=gvosc)
I_volosctypech  = input.string('On Balance Volume',           'Volume Oscillator Type for Chikou',         options=['TFS Volume Oscillator', 'On Balance Volume', 'Klinger Volume Oscillator', 'Cumulative Volume Oscillator', 'Volume Zone Oscillator'], group=gvosc)
I_volumelen     = input.int(30,                               'Volume Length',                             minval=1,             group=gvosc)
I_volzonelen    = input.int(21,                               'Volume Zone Length',                        minval=1,             group=gvosc)
I_volfastlen    = input.int(34,                               'Volume Fast Length',                        minval=1,             group=gvosc)
I_volslowlen    = input.int(55,                               'Volume Slow Length',                        minval=1,             group=gvosc)
I_rvolumetype   = input.string(pvtstr,                        'Relative Volume Strength Index Type',       options=[obvstr, cvdstr, pvtstr], group=gvrindex)
I_rvolumelen    = input.int(14,                               'Relative Volume Strength Index Length',     minval=1,             group=gvrindex)
I_volpeak       = input.int(50,                               'Relative Volume Strength Index Peak',       minval=1,             group=gvrindex)
i_emalen1       = input.int(8,                                'EMA 1 Length',                              minval=1,             group=gvrindex)
i_emalen2       = input.int(21,                               'EMA 2 Length',                              minval=1,             group=gvrindex)
i_atrfastlen    = input.int(14,                               'Average True Range Fast Length',                                  group=gatr)
i_atrslowlen    = input.int(46,                               'Average True Range Slow Length',                                  group=gatr)
i_showsr        = input.bool(true,                            'Show Supports/Resistances?',                                      group=gsr)
i_rescol        = input.color(color.new(color.red,   0),      'Resistance Color',                                                group=gsr,     inline='srcol')
i_supcol        = input.color(color.new(color.green, 0),      'Support Color',                                                   group=gsr,     inline='srcol')
i_maxline       = input.int(2,                                'Maximum Lines',                             minval=1, maxval=500, group=gsr,     inline='lines')
i_layout        = input.string('Wick',                        'Lines Type',                                options=['Wick', 'Zone'], group=gsr, inline='lines')
i_linewidth     = input.int(1,                                'Lines Width     ',                          minval=1, maxval=3,   group=gsr,     inline='lines style')
i_linestyle     = input.string('Solid',                       'Lines Style',                               options=['Solid', 'Dotted', 'Dashed'], group=gsr, inline='lines style')
i_extend        = input.bool(true,                            'Extend Lines',                                                    group=gsr,     inline='lines style')
i_psylevels     = input(false,                                'Display Psychological Levels?',                                   group=gpsylvl)
i_linescounter  = input(4,                                    'Lines Above/Below',                                               group=gpsylvl)
i_psylinescol   = input.color(color.new(color.gray,  0),      'Lines Color',                                                     group=gpsylvl)
i_showbc        = input.bool(true,                            'Use Bar Color?',                                                  group=gbarcol)
i_bearbarcol    = input.color(color.new(#910000,     0),      'Bear',                                                            group=gbarcol, inline='barcolor')
i_bullbarcol    = input.color(color.new(#006400,     0),      'Bull',                                                            group=gbarcol, inline='barcolor')
i_consobarcol   = input.color(color.new(#FF9800,     0),      'Consolidation',                                                   group=gbarcol, inline='barcolor')
i_neutralbarcol = input.color(color.new(#000000,   100),      'Neutral',                                                         group=gbarcol, inline='barcolor')
i_showtable     = input(true,                                 'Show Panel?',                                                     group=gtable)
i_tablepresets  = input.string('Custom',                      'Presets',                                   options=['Custom', '9/26/52/26     - 6D Markets (Default)', '8/22/44/22     - 5D Markets', '9/30/60/30     - 24h/7D Markets (Crypto)', '20/60/120/60   - 24h/7D Markets (Slow Version)'], group=gtable)
i_tableloc      = input.string('Bottom Right',                'Position',                                  options=['Bottom Right', 'Top Right', 'Bottom Left', 'Top Left', 'Top', 'Right', 'Bottom', 'Left'], group=gtable)
i_tabletxtsize  = input.string('Small',                       'Text Size',                                 options=['Tiny', 'Small', 'Normal', 'Large'], group=gtable)
i_tabletxtcol   = input.color(color.new(color.white, 0),      'Text Color',                                                      group=gtable)
i_tablebgcol    = input.color(color.new(#696969,    80),      'Background Color',                                                group=gtable)
i_tabletkpc     = input.bool(true,                            'Display Tenkan-Sen Price Cross?',                                 group=gtable)
i_tablekjpc     = input.bool(true,                            'Display Kijun-Sen Price Cross?',                                  group=gtable)
i_tablechpc     = input.bool(true,                            'Display Chikou Span Price Cross?',                                group=gtable)
i_tablekumobr   = input.bool(true,                            'Display Kumo Breakout?',                                          group=gtable)
i_tablekumotw   = input.bool(true,                            'Display Kumo Twist?',                                             group=gtable)

//◀─── Volume Heikin Ashi Calculation ───►

//Constant Price Variables

haclose = i_uha ? ohlc4 : close
vol     = volume

//Heikin Ashi Function

f_openha() =>
    haopen  = float(na)
    haopen := na(haopen[1]) ? (open + close) / 2 : (nz(haopen[1]) + nz(haclose[1])) / 2
    haopen

//Conditions

haopen    = i_uha ? f_openha() : open
haboolean = f_openha()

//◀─── Price Variables Calculation ───►

//Price Variables Declaration

open_      = open
high_      = high
low_       = low
close_     = close
bar_index_ = bar_index

//Alternative Price Variable Declaration

alternativesrc = i_altsrc == '(open + close +3 * (high + low)) / 8'      ? (open + close +3 * (high  + low)) / 8             :
     i_altsrc             == 'close + high + low -2 * open'              ? close + high + low -2 * open                      :
     i_altsrc             == '(close +5 * (high + low) -7 * (open)) / 4' ? (close        +5 * (high  + low) -7 * (open)) / 4 :
     i_altsrc             == '(open + close +5 * (high + low)) / 12'     ? (open + close +5 * (high  + low)) / 12            :
     i_altsrc             == '(close > open ? high : low)'               ? (close > open      ? high : low)                  :
     i_altsrc             == 'Heiken-Ashi'                               ? (ohlc4 > haboolean ? high : low)                  :
     i_src

altsrcres = i_wmas ? ta.swma(alternativesrc) : alternativesrc

//◀─── Global Functions ───►

//Color Call Function

fzonecolor(srcolor, _call) =>
    c1 = color.r(srcolor)
    c2 = color.g(srcolor)
    c3 = color.b(srcolor)
    color.rgb(c1, c2, c3, _call)

//Lines Styles String Function

f_i_linestyle(_style) =>
    _style == 'Solid' ? line.style_solid : _style == 'Dotted' ? line.style_dotted : line.style_dashed

//Volume Oscillator Functions

f_patternrate(cond, tw, bw, body) =>
    ret  = 0.5 * (tw + bw + (cond ? 2 * body : 0)) / (tw + bw + body)
    ret := nz(ret) == 0 ? 0.5 : ret
    ret

f_volcalc(vol_src, _open, _high, _low, _close) =>
    float result = 0
    tw           = _high - math.max(_open, _close)
    bw           = math.min(_open,  _close) - _low
    body         = math.abs(_close - _open)
    deltaup      = vol_src * f_patternrate(_open <= _close, tw, bw, body)
    deltadown    = vol_src * f_patternrate(_open  > _close, tw, bw, body)
    delta        = _close >= _open ? deltaup : -deltadown
    cumdelta     = ta.cum(delta)
    float ctl    = na
    ctl         := cumdelta
    cv           = I_rvolumetype == obvstr ? ta.obv : I_rvolumetype == cvdstr ? ctl : ta.pvt
    ema1         = ta.ema(cv, i_emalen1)
    ema2         = ta.ema(cv, i_emalen2)
    result      := ema1 - ema2
    result

f_zone(_src, _type, _len) =>
    vp = _src > _src[1] ? _type : _src < _src[1] ? -_type : _src == _src[1] ? 0 : 0
    z  = 100 * (ta.ema(vp, _len) / ta.ema(_type, _len))
    z

f_volzosc(vol_src, _close) =>
    float result = 0
    zLen         = I_volzonelen
    result      := f_zone(_close, vol_src, zLen)
    result

f_volosc(type, vol_src, vol_len, _open, _high, _low, _close) =>
    float result = 0
    if type == 'TFS Volume Oscillator'
        iff_1    = _close < _open ? -vol_src : 0
        naccvol  = math.sum(_close > _open ? vol_src : iff_1, vol_len)
        result  := naccvol / vol_len
        result
    if type == 'On Balance Volume'
        result  := ta.cum(math.sign(ta.change(_close)) * vol_src)
        result
    if type == 'Klinger Volume Oscillator'
        fastx    = I_volfastlen
        slowx    = I_volslowlen
        xtrend   = _close > _close[1] ? vol * 100 : -vol * 100
        xfast    = ta.ema(xtrend, fastx)
        xslow    = ta.ema(xtrend, slowx)
        result  := xfast - xslow
        result
    if type == 'Cumulative Volume Oscillator'
        result  := f_volcalc(vol_src, _open, _high, _low, _close)
        result
    if type == 'Volume Zone Oscillator'
        result  := f_volzosc(vol_src, _close)
        result
    result

//Kijun V2 Function

f_kjv2(src, len) =>
    var float result = 0.0
    kijun            = math.avg(ta.lowest(len), ta.highest(len))
    conversionLine   = math.avg(ta.lowest(len   / i_kjdivider), ta.highest(len / i_kjdivider))
    delta            = (kijun + conversionLine) / 2
    result          := delta
    result

//◀─── Relative Volume Strength Index calculation ───►

tksignal  = f_volosc(I_volosctypetk, vol, I_rvolumelen, haopen, high, low, haclose)
kjsignal  = f_volosc(I_volosctypekj, vol, I_rvolumelen, haopen, high, low, haclose)
sksignal  = f_volosc(I_volosctypesk, vol, I_rvolumelen, haopen, high, low, haclose)
chsignal  = f_volosc(I_volosctypech, vol, I_rvolumelen, haopen, high, low, haclose)
sumsignal = f_volosc(I_volosctype,   vol, I_rvolumelen, haopen, high, low, haclose)

//◀─── Volume Breakout Calculation ───►

tkbrvoldn = tksignal < I_volpeak
tkbrvolup = tksignal > I_volpeak
kjbrvoldn = kjsignal < I_volpeak
kjbrvolup = kjsignal > I_volpeak
skbrvoldn = sksignal < I_volpeak
skbrvolup = sksignal > I_volpeak
chbrvoldn = chsignal < I_volpeak
chbrvolup = chsignal > I_volpeak

//Conditions

signalbrvoldn = sumsignal < I_volpeak
signalbrvolup = sumsignal > I_volpeak

//◀─── Volatility Strength ───►

atrvolmeter = ta.atr(i_atrfastlen) > ta.atr(i_atrslowlen)

//Adaptive Chikou Function

f_chikou(float src, simple int len, float _high, float _low, color bull_col, color bear_col, color r_col) =>
    var isup       = bool(na)
    var isdown     = bool(na)
    var _re        = bool(na)
    var sig        = int(na)
    var color _clr = color.new(na, 0)
    isup          := src > ta.highest(_high, len)[len]
    isdown        := src < ta.lowest(_low,   len)[len]
    _re           := src < ta.highest(_high, len)[len] and src > ta.lowest(_low, len)[len]
    _clr          := isdown ? bear_col : isup ? bull_col : r_col
    sig           := isup ? 1 : isdown ? -1 : 0
    [_clr, sig]

[chikou_clr, chikoufiltersig] = f_chikou(altsrcres, i_chfiltper, high, low, i_chbullcol, i_chbearcol, i_chconsocol)

//Boolean Settings Functions

f_boolean(chka, reference_a) =>
	var result_bool      = bool(na)
    for i                = 0 to array.size(chka) -1
        if array.get(chka, i) == true
            result_bool := array.get(reference_a, i)
            break

	for i = 0 to array.size(chka) -1
		result_bool := array.get(chka, i) ? array.get(reference_a, i) and result_bool : result_bool
	result_bool

bool[] tkarray    = array.from(i_tkvolsetup,
     i_tkatrsetup,
     i_tkchfilter)
bool[] kjarray    = array.from(i_kjvolsetup,
     i_kjatrsetup,
     i_kjchfilter)
bool[] skarray    = array.from(I_skvolsetup,
     I_atrsetup,
     I_skchfilter)
bool[] charray    = array.from(i_chvolsetup,
     i_chatrsetup,
     i_chtfilter)
bool[] tkvolarray = array.from(tkbrvolup,
     atrvolmeter,
     chikoufiltersig ==  1)
bool[] kjvolarray = array.from(kjbrvolup,
     atrvolmeter,
     chikoufiltersig ==  1)
bool[] skvolarray = array.from(skbrvolup,
     atrvolmeter,
     chikoufiltersig ==  1)
bool[] chvolarray = array.from(chbrvolup,
     atrvolmeter,
     chikoufiltersig ==  1)

//Boolean to Conditions

booltkup = f_boolean(tkarray, tkvolarray)
boolkjup = f_boolean(kjarray, kjvolarray)
boolchup = f_boolean(charray, chvolarray)
boolskup = f_boolean(skarray, skvolarray)

//Dynamic Length Function

f_dyn(bool para, float adapt_Pct, simple int minLength, simple int maxLength) =>
    var dyna_len    = int(na)
    var float i_len = math.avg(minLength, maxLength)
    i_len          := para ? math.max(minLength, i_len * adapt_Pct) : math.min(maxLength, i_len * (2-adapt_Pct))
    dyna_len       := int(i_len)
    dyna_len

//Dynamic Length Conditions

dyntk = f_dyn(booltkup,  i_tkdynperc, i_tkminlen, i_tkmaxlen)
dynkj = f_dyn(boolkjup , i_kjdynperc, i_kjminlen, i_kjmaxlen)
dynsk = f_dyn(boolskup , I_skperc,    I_skminlen, I_skmaxlen)
dynch = f_dyn(boolchup , i_chdynperc, i_chminlen, i_chmaxlen)

//◀─── Index Calculation ───►

tenkansen  = f_kjv2(altsrcres, dyntk)
kijunsen   = f_kjv2(altsrcres, dynkj)
senkoua    = math.avg(tenkansen, kijunsen)
senkoub    = math.avg(ta.highest(high, dynsk), ta.lowest(low, dynsk))
chikouspan = altsrcres

//Tenkan-Sen/kijun-Sen Boolean Condition to Float

var float tkbool = na
if tenkansen
    tkbool := tenkansen
    tkbool
var float kjbool = na
if kijunsen
    kjbool := kijunsen
    kjbool

//◀─── Dynamic Type Calculation ───►

bearmomentum = ta.mom(altsrcres, dynch - 1) < 0
bullmomentum = ta.mom(altsrcres, dynch - 1) > 0
cloudhigh    = math.max(senkoua[I_skoffset - 1], senkoub[I_skoffset - 1])
cloudlow     = math.min(senkoua[I_skoffset - 1], senkoub[I_skoffset - 1])
pbk          = altsrcres < cloudhigh
pak          = altsrcres > cloudhigh

//Boolean Arrays Variables

bool[] indexarray = array.from(I_volsetup,
     I_atrvolatility,
     I_tkkjcross,
     I_chmom,
     I_chgtp,
     I_pgtk,
     I_pgttk,
     I_pgtchf)

bool[] downvolumearray = array.from(signalbrvoldn,
     atrvolmeter,
     tenkansen  < kijunsen,
     bearmomentum,
     chikouspan < altsrcres[int(dynch)],
     pbk,
     altsrcres  < tenkansen,
     chikoufiltersig == -1)

bool[] uppervolumearray = array.from(signalbrvolup,
     atrvolmeter,
     tenkansen  > kijunsen,
     bullmomentum,
     chikouspan > altsrcres[int(dynch)],
     pak,
     tenkansen,
     chikoufiltersig ==  1)

//Conditions Calculation

bearfilterdn  = f_boolean(indexarray,  downvolumearray)
bullfilterup  = f_boolean(indexarray, uppervolumearray)
bearcondition = I_tkeqkj ? tenkansen == kijunsen and tenkansen[1] > kijunsen[1] or bearfilterdn : bearfilterdn
bullcondition = I_tkeqkj ? tenkansen == kijunsen and tenkansen[1] < kijunsen[1] or bullfilterup : bullfilterup
isdown        = bearcondition
isup          = bullcondition

//Conditions

sell = isdown and not isdown[1]
buy  = isup   and not   isup[1]

//Sig Calculation

var sig  =  0
if sell and sig >= 0
    sig := -1
if buy  and sig <= 0
    sig :=  1

//Conditions

shortcr = sig == -1 and sig[1] != -1
longcr  = sig ==  1 and sig[1] !=  1

//Boolean Conditions to Float

var float ichibearprice = na
if shortcr
    ichibearprice := bar_index
    ichibearprice
var float ichibullprice = na
if longcr
    ichibullprice := bar_index
    ichibullprice

//Labels Calculation

if i_showlabels
    l = ta.change(ichibearprice) ? label.new(bar_index, ichibearprice[1] + 0.01, str.tostring(math.round_to_mintick(high)), color=color.new(color.black, 0), textcolor=color.new(color.white, 0), style=label.style_label_down, yloc=yloc.abovebar, size=size.small) : ta.change(ichibullprice) ? label.new(bar_index, ichibullprice[1] - 0.01, str.tostring(math.round_to_mintick(low)), color=color.new(color.black, 0), textcolor=color.new(color.white, 0), style=label.style_label_up, yloc=yloc.belowbar, size=size.small) : na
    l

//Volatility Coordination Constant Variable

atrxy = 0.85 * ta.atr(5)

//◀─── Plotting ───►

plotshape(i_showlabels and shortcr ? (high) + atrxy : na, style=shape.triangledown, color=i_rescol, location=location.absolute, size=size.small)
plotshape(i_showlabels and longcr  ? (low)  - atrxy : na, style=shape.triangleup,   color=i_supcol, location=location.absolute, size=size.small)
plotshape(i_showlabels and sell    ? high   + atrxy : na, style=shape.circle,       color=i_rescol, location=location.absolute,  size=size.tiny)
plotshape(i_showlabels and buy     ? low    - atrxy : na, style=shape.circle,       color=i_supcol, location=location.absolute,  size=size.tiny)

plotchikouspan = plot(chikouspan, title='Chikou', color=not i_showchikou ? na : chikou_clr, linewidth=1, offset=-dynch)

f_colorgradient(_source, _min, _max, _cbear, _cbull) =>
    var float _center = _min + (_max - _min) / 2
    color     _return = _source >= _center ?
      color.from_gradient(_source, _min,    _center, color.new(_cbear,   0), color.new(_cbear, 100)) :
      color.from_gradient(_source, _center, _max,    color.new(_cbull, 100), color.new(_cbull,   0))

skclr  = (senkoua - senkoub) / altsrcres * 100
skfill = senkoua > senkoub ? color.from_gradient(ta.rsi(math.avg(senkoua , senkoub), 14) , 0, 100, I_skconso, I_skbull) :
     senkoua     < senkoub ? color.from_gradient(ta.rsi(math.avg(senkoua , senkoub), 14) , 0, 100, I_skconso, I_skbear) : I_skconso

plottenkansen = plot(tenkansen, title='Tenkan-Sen',    color=not i_showtenkan ? na : i_tenkancolor, linewidth=1,            offset=0)
plotkijunsen  = plot(kijunsen,  title='Kijun-Sen',     color=not i_showkijun  ? na : i_kijuncolor,  linewidth=1,            offset=0)
plotsenkoua   = plot(senkoua,   title='Senkou-Span A', color=not I_ska        ? na : I_skbull,      linewidth=1, offset=I_skoffset-1)
plotsenkoub   = plot(senkoub,   title='Senkou-Span B', color=not I_skb        ? na : I_skbear,      linewidth=1, offset=I_skoffset-1)
fill(plotsenkoua, plotsenkoub,  color=not I_kumofill ? na : color.new(skfill, I_kumofillt))

//Tenkan-Sen/kijun-Sen Price Plotting

if i_showtenkan and i_showprice
    l1 = label.new(bar_index, tkbool, 'Tenkan-Sen - ' + str.tostring(math.round_to_mintick(tkbool)), color=color.new(i_tenkancolor, 100), textcolor=color.new(i_tenkancolor, 0), style=label.style_label_left, yloc=yloc.price, size=size.small)
    l1
    label.delete(l1[1])
if i_showkijun  and i_showprice
    l1 = label.new(bar_index, kjbool, 'Kijun-Sen - '  + str.tostring(math.round_to_mintick(kjbool)), color=color.new(i_kijuncolor, 100),  textcolor=color.new(i_kijuncolor, 0),  style=label.style_label_left, yloc=yloc.price, size=size.small)
    l1
    label.delete(l1[1])

//Bar Color Plotting

colbar(src, tenkansen, kijunsen) =>
    vbarcolor  = color.new(na, 0)
    vbarcolor := src > tenkansen and src > kijunsen?  i_bullbarcol :
         src < tenkansen and src < kijunsen ? i_bearbarcol         :
         src > tenkansen and src < kijunsen ? i_consobarcol        :
         src < tenkansen and src > kijunsen ? i_neutralbarcol      : na
    vbarcolor

bc = colbar(close, tenkansen, kijunsen)

barcolor(i_showbc ? bc : na)

//◀─── Kijun-Sen Hidden Supports/Resistances ───►

//Smoothed MA Calculation

smma1  = 0.0
smma2  = 0.0
smakj1 = ta.sma(close, i_kjminlen)
smakj2 = ta.sma(close, i_kjmaxlen)
smma1 := na(smma1[1]) ? smakj1 : (smma1[1] * (20 - 1) + close) / 20
smma2 := na(smma2[1]) ? smakj2 : (smma2[1] * (60 - 1) + close) / 60

//Kijun-Sen Calculation

kjsmma = math.avg(ta.lowest(26), ta.highest(26))

//Conditions

hiddenbearkj = ta.crossunder(smma1, kjsmma) and kjsmma < smma2 and close < smma1
hiddenbullkj = ta.crossover(smma1,  kjsmma) and kjsmma > smma2 and close > smma1

//Boolean Conditions to Float

var float bearhkj = na
if hiddenbearkj
    bearhkj := high
    bearhkj
var float bullhkj = na
if hiddenbullkj
    bullhkj := low
    bullhkj

//Labels Calculation

if i_showkjhlabels
    l = ta.change(bearhkj) ? label.new(bar_index, bearhkj[1] + 0.01, str.tostring(math.round_to_mintick(bearhkj)), color=color.new(color.black, 0), textcolor=color.new(color.white, 0), style=label.style_label_down, yloc=yloc.abovebar, size=size.small) : ta.change(bullhkj) ? label.new(bar_index, bullhkj[1] - 0.01, str.tostring(math.round_to_mintick(bullhkj)), color=color.new(color.black, 0), textcolor=color.new(color.white, 0), style=label.style_label_up, yloc=yloc.belowbar, size=size.small) : na
    l

//Plotting

plotshape(i_showkjhlabels ? hiddenbearkj : na, title='Bearish Hidden Kijun-Sen', style=shape.triangledown, location=location.abovebar, color=i_rescol, size=size.tiny)
plotshape(i_showkjhlabels ? hiddenbullkj : na, title='Bullish Hidden Kijun-Sen', style=shape.triangleup,   location=location.belowbar, color=i_supcol, size=size.tiny)

//Hidden Kijun S/R Variables Declaration

var int     numberofline2       = i_maxline
var float   upperphzone2        = na
var float   upperplzone2        = na
var float   lowerphzone2        = na
var float   lowerplzone2        = na
var line[]  upperphzonearr2     = array.new_line(0, na)
var line[]  upperplzonearr2     = array.new_line(0, na)
var line[]  lowerphzonearr2     = array.new_line(0, na)
var line[]  lowerplzonearr2     = array.new_line(0, na)
var line    upperphzoneline2    = na
var line    upperplzoneline2    = na
var line    lowerphzoneline2    = na
var line    lowerplzoneline2    = na
var bool[]  upperzonetestedarr2 = array.new_bool(0, false)
var bool[]  lowerzonetestedarr2 = array.new_bool(0, false)
var bool    upperzonetested2    = false
var bool    lowerzonetested2    = false
var bool    nobool2             = true
var color   upperzonecolor2     = color.red
var color   lowerzonecolor2     = color.green
var label[] labelpharr2         = array.new_label(0, na)
var label[] labelplarr2         = array.new_label(0, na)
var label   labelph2            = na
var label   labelpl2            = na

//Hidden Kijun Resistances Calculation

if i_showsr and i_showkjhsr and hiddenbearkj
    upperphzone2     := high_
    upperplzone2     := close_ < open_     ? close_ : open_
    upperplzoneline2 := i_layout == 'Zone' ? line.new(bar_index_,  upperplzone2, bar_index, upperplzone2, width=i_linewidth) : na
    upperphzoneline2 := nobool2            ? line.new(bar_index_,  upperphzone2, bar_index, upperphzone2, width=i_linewidth) : line.new(bar_index_, (upperphzone2 + upperplzone2) / 2, bar_index, (upperphzone2 + upperplzone2) / 2, width=i_linewidth)
    labelph2         := i_showsr           ? label.new(bar_index_, nobool2 ? upperphzone2 : (upperphzone2 + upperplzone2) / 2, text=str.tostring(bar_index - bar_index_), textcolor=upperzonecolor2, style=label.style_none) : na
    if array.size(upperphzonearr2) > numberofline2
        line.delete(array.shift(upperphzonearr2))
        line.delete(array.shift(upperplzonearr2))
        array.shift(upperzonetestedarr2)
        label.delete(array.shift(labelpharr2))
    array.push(upperphzonearr2, upperphzoneline2)
    array.push(upperplzonearr2, upperplzoneline2)
    array.push(upperzonetestedarr2,       i_extend ? true : false)
    array.push(labelpharr2,             labelph2)
if array.size(upperplzonearr2) > 0
    for i = 0 to array.size(upperplzonearr2) - 1 by 1
        line  tempupperline2  = array.get(upperphzonearr2,     i)
        line  templowerline2  = array.get(upperplzonearr2,     i)
        label linepricelabel2 = array.get(labelpharr2,         i)
        bool  tested2         = array.get(upperzonetestedarr2, i)
        line.set_style(tempupperline2, f_i_linestyle(i_linestyle))
        line.set_style(templowerline2, f_i_linestyle(i_linestyle))
        line.set_color(tempupperline2, color.from_gradient(i,       1, numberofline2, fzonecolor(upperzonecolor2, 00), fzonecolor(upperzonecolor2, 00)))
        line.set_color(templowerline2, color.from_gradient(i,       1, numberofline2, fzonecolor(upperzonecolor2, 00), fzonecolor(upperzonecolor2, 00)))
        label.set_textcolor(linepricelabel2, color.from_gradient(i, 1, numberofline2, fzonecolor(upperzonecolor2, 00), upperzonecolor2))
        label.set_text(linepricelabel2, str.tostring(math.round_to_mintick(line.get_y1(tempupperline2))))
        label.set_text(linepricelabel2, '                                                                Hidden Kijun Resistance - ' + str.tostring(math.round_to_mintick(line.get_y1(tempupperline2))))
        label.set_x(linepricelabel2, bar_index)
        crossed = high > line.get_y1(tempupperline2)
        if crossed and not tested2
            array.set(upperzonetestedarr2, i, true)
            label.delete(linepricelabel2)
        else if i_extend ? tested2 : not tested2
            line.set_x2(tempupperline2, bar_index)
            array.set(upperphzonearr2, i, tempupperline2)
            line.set_x2(templowerline2, bar_index)
            array.set(upperplzonearr2, i, templowerline2)

//Hidden Kijun Supports Calculation

if i_showsr and i_showkjhsr and hiddenbullkj
    lowerplzone2     := low_
    lowerphzone2     := close_ < open_     ? open_ : close_
    lowerphzoneline2 := i_layout == 'Zone' ? line.new(bar_index_,  lowerphzone2, bar_index, lowerphzone2, width=i_linewidth) : na
    lowerplzoneline2 := nobool2            ? line.new(bar_index_,  lowerplzone2, bar_index, lowerplzone2, width=i_linewidth) : line.new(bar_index_, (lowerphzone2 + lowerplzone2) / 2, bar_index, (lowerphzone2 + lowerplzone2) / 2, width=i_linewidth)
    labelpl2         := i_showsr           ? label.new(bar_index_, nobool2 ? lowerplzone2 : (lowerphzone2 + lowerplzone2) / 2, text=str.tostring(bar_index - bar_index_), textcolor=lowerzonecolor2, style=label.style_none) : na
    if array.size(lowerphzonearr2) > numberofline2
        line.delete(array.shift(lowerphzonearr2))
        line.delete(array.shift(lowerplzonearr2))
        array.shift(lowerzonetestedarr2)
        label.delete(array.shift(labelplarr2))
    array.push(lowerphzonearr2, lowerphzoneline2)
    array.push(lowerplzonearr2, lowerplzoneline2)
    array.push(lowerzonetestedarr2,       i_extend ? true : false)
    array.push(labelplarr2,             labelpl2)
if array.size(lowerplzonearr2) > 0
    for i = 0 to array.size(lowerplzonearr2) - 1 by 1
        line  tempupperline2  = array.get(lowerphzonearr2,     i)
        line  templowerline2  = array.get(lowerplzonearr2,     i)
        label linepricelabel2 = array.get(labelplarr2,         i)
        bool  tested2         = array.get(lowerzonetestedarr2, i)
        line.set_style(tempupperline2, f_i_linestyle(i_linestyle))
        line.set_style(templowerline2, f_i_linestyle(i_linestyle))
        line.set_color(tempupperline2, color.from_gradient(i,       1, numberofline2, fzonecolor(lowerzonecolor2, 00), fzonecolor(lowerzonecolor2, 00)))
        line.set_color(templowerline2, color.from_gradient(i,       1, numberofline2, fzonecolor(lowerzonecolor2, 00), fzonecolor(lowerzonecolor2, 00)))
        label.set_textcolor(linepricelabel2, color.from_gradient(i, 1, numberofline2, fzonecolor(lowerzonecolor2, 00), lowerzonecolor2))
        label.set_text(linepricelabel2, str.tostring(math.round_to_mintick(line.get_y1(templowerline2))))
        label.set_text(linepricelabel2, '                                                            Hidden Kijun Support - ' + str.tostring(math.round_to_mintick(line.get_y1(templowerline2))))
        label.set_x(linepricelabel2, bar_index)
        crossed = low < line.get_y1(templowerline2)
        if crossed and not tested2
            array.set(lowerzonetestedarr2, i, true)
            label.delete(linepricelabel2)
        else if i_extend ? tested2 : not tested2
            line.set_x2(tempupperline2, bar_index)
            array.set(lowerphzonearr2, i, tempupperline2)
            line.set_x2(templowerline2, bar_index)
            array.set(lowerplzonearr2, i, templowerline2)

//◀─── Table Calculation ───►

//Constant Colors Variables

itablestrongbearcol  = color.new(color.red,    0)
itablebearcol        = color.new(color.maroon, 0)
itableneutralbearcol = color.new(color.silver, 0)
itablestrongbullcol  = color.new(color.lime,   0)
itablebullcol        = color.new(color.green,  0)
itableneutralbullcol = color.new(color.silver, 0)
itableconsocol       = color.new(color.orange, 0)

//Global Functions

donchian(len) =>
    math.avg(ta.lowest(len), ta.highest(len))

f_presets(p) =>
    if p == 'Custom'
        [i_tkminlen, i_kjminlen, I_skminlen, I_skoffset]
    else if p == '9/26/52/26     - 6D Markets (Default)'
        [9, 26, 52, 26]
    else if p == '8/22/44/22     - 5D Markets'
        [8, 22, 44, 22]
    else if p == '9/30/60/30     - 24h/7D Markets (Crypto)'
        [10, 30, 60, 30]
    else if p == '20/60/120/60   - 24h/7D Markets (Slow Version)'
        [20, 60, 120, 60]
    else
        [0, 0, 0, 0]

f_tablestringpos(p) =>
    p == 'Bottom Right' ? position.bottom_right : p == 'Top Right' ? position.top_right : p == 'Bottom Left' ? position.bottom_left : p == 'Top Left' ? position.top_left : p == 'Top' ? position.top_center : p == 'Right' ? position.middle_right : p == 'Bottom' ? position.bottom_center : p == 'Left' ? position.middle_left : na

f_tablestringdirsym(trend) =>
    trend == 1 ? '▲ Strong' : trend == 2 ? '▲ Neutral' : trend == 3 ? '▲ Weak' : trend == -1 ? '▼ Strong' : trend == -2 ? '▼ Neutral' : trend == -3 ? '▼ Weak' : '■ Consolidation'

f_tablestringcolor(trend, c_up, c_down, c_consolidation) =>
    trend > 0 ? c_up : trend < 0 ? c_down : c_consolidation

f_tablestringcolordir(trend, c_up, c_down) =>
    trend > 0 ? c_up : c_down

f_tablecloudtrend(l1, l2) =>
    l1 > l2 ? 1 : -1

f_tabletrendsum(sum, count) =>
    sum == count ? 1 : sum == -count ? -1 : 0

f_tablestrengthconditions(pos, uptrend) =>
    uptrend ? pos == 1 ? 1 : pos == 0 ? 2 : 3 : pos == -1 ? -1 : pos == 0 ? -2 : -3

//Presets Calculation

[i_conversion_len, i_base_len, i_lagging_len, i_offset] = f_presets(i_tablepresets)

//Presets Conditions

i_conversion = donchian(i_conversion_len)
i_base       = donchian(i_base_len)
i_lead1      = math.avg(i_conversion, i_base)
i_lead2      = donchian(i_lagging_len)
i_cloud_top2 = math.max(i_lead1,     i_lead2)
i_cloud_bot2 = math.min(i_lead1,     i_lead2)

//Constant Array Variable

tablearrindex = array.new_int(0)

//Signals Conditions Function

f_tableconditions(enabled, signal) =>
    if enabled
        array.push(tablearrindex, signal)

//Calculation

i_lead1_current         = i_lead1[i_offset - 1]
i_lead2_current         = i_lead2[i_offset - 1]
i_cloud_top             = math.max(i_lead1_current, i_lead2_current)
i_cloud_bot             = math.min(i_lead1_current, i_lead2_current)
table_base_position     = i_base > i_cloud_top ? 1 : i_base < i_cloud_bot ? -1 : 0
table_base_breakout     = close > i_base          ? f_tablestrengthconditions(table_base_position,   true) : f_tablestrengthconditions(table_base_position,   false)
table_cloud2_trend      = f_tablecloudtrend(i_conversion, i_base)
table_cloud2_top        = math.max(i_base, i_conversion)
table_cloud2_bot        = math.min(i_base, i_conversion)
table_cloud2_position   = table_cloud2_bot > i_cloud_top ? 1 : table_cloud2_top < i_cloud_bot ? -1 : 0
table_cloud2_cross      = table_cloud2_trend == 1 ? f_tablestrengthconditions(table_cloud2_position, true) : f_tablestrengthconditions(table_cloud2_position, false)
table_lagging_lead1     = i_lead1_current[i_offset - 1]
table_lagging_lead2     = i_lead2_current[i_offset - 1]
table_lagging_cloud_top = math.max(table_lagging_lead1, table_lagging_lead2)
table_lagging_cloud_bot = math.min(table_lagging_lead1, table_lagging_lead2)
table_lagging_high      = high[i_offset - 1]
table_lagging_low       = low[i_offset  - 1]
table_lagging_trend     = close > table_lagging_high ? 1 : close < table_lagging_low ? -1 : 0
table_lagging_position  = close > i_cloud_top        ? 1 : close < i_cloud_bot       ? -1 : 0
table_lagging_cross     = table_lagging_trend == 1   ? f_tablestrengthconditions(table_lagging_position, true) : table_lagging_trend == -1 ? f_tablestrengthconditions(table_lagging_position, false) : 0
table_cloud_breakout    = close > i_cloud_top        ? 1 : close < i_cloud_bot       ? -1 : 0
table_cloud_trend       = f_tablecloudtrend(i_lead1, i_lead2)
table_lead_cross        = table_cloud_trend   == 1   ? f_tablestrengthconditions(table_cloud_breakout,   true) :                               f_tablestrengthconditions(table_cloud_breakout, false)

//Conditions Functions

f_tableconditions(i_tablekjpc,    table_base_breakout)
f_tableconditions(i_tabletkpc,     table_cloud2_cross)
f_tableconditions(i_tablechpc,    table_lagging_cross)
f_tableconditions(i_tablekumobr, table_cloud_breakout)
f_tableconditions(i_tablekumotw,     table_lead_cross)

//Conditions

table_signal_max              = array.max(tablearrindex)
table_signal_min              = array.min(tablearrindex)
table_signal                  = table_signal_min > 0 ? table_signal_max : table_signal_max < 0 ? table_signal_min : 0
table_changed                 = table_signal != table_signal[1]
table_downtrend               = table_changed       and table_signal    == -1
table_uptrend                 = table_changed       and table_signal    ==  1
table_consolidation           = table_changed       and table_signal    ==  0
table_consolidation_downtrend = table_consolidation and table_signal[1] == -1
table_consolidation_uptrend   = table_consolidation and table_signal[1] ==  1

//Colors String Function

f_tablecolors(t) =>
    t == 1 ? itablestrongbullcol : t == 2 ? itableneutralbullcol : t == 3 ? itablebullcol : t == -1 ? itablestrongbearcol : t == -2 ? itableneutralbearcol : t == -3 ? itablebearcol : itableconsocol

//String to Variable

i_panel_c_signal_text = f_tablecolors(table_signal)

//Table Text Size String Function

tabletxtwi = i_tabletxtsize == 'Tiny' ? size.tiny : i_tabletxtsize == 'Small' ? size.small : i_tabletxtsize == 'Normal' ? size.normal : i_tabletxtsize == 'Large' ? size.large : size.normal

//Plotting

var table i_panel = na
insertRow(i, text_1, trend, col) =>
    table.cell(i_panel, 0, i, text_1,                     text_color=col,                  text_halign=text.align_right, text_size=tabletxtwi)
    table.cell(i_panel, 1, i, f_tablestringdirsym(trend), text_color=f_tablecolors(trend), text_halign=text.align_left,  text_size=tabletxtwi)
    i + 1

if i_showtable and array.size(tablearrindex) > 0
    i_panel := table.new(position=f_tablestringpos(i_tableloc), columns=2, rows=20, bgcolor=i_tablebgcol, border_width=0)
    i = 0

    if i_tabletkpc
        i := insertRow(i, 'Tenkan-Sen Price Cross',  table_cloud2_cross,   i_tabletxtcol)
        i

    if i_tablekjpc
        i := insertRow(i, 'Kijun-Sen Price Cross',   table_base_breakout,  i_tabletxtcol)
        i

    if i_tablechpc
        i := insertRow(i, 'Chikou Span Price Cross', table_lagging_cross,  i_tabletxtcol)
        i

    if i_tablekumobr
        i := insertRow(i, 'Kumo Breakout',           table_cloud_breakout, i_tabletxtcol)
        i

    if i_tablekumotw
        i := insertRow(i, 'Kumo Twist',              table_lead_cross,     i_tabletxtcol)
        i

    table.cell(i_panel, 0, i, 'Status',                          bgcolor=i_tablebgcol, text_color=i_panel_c_signal_text, text_halign=text.align_right, text_size=tabletxtwi)
    table.cell(i_panel, 1, i, f_tablestringdirsym(table_signal), bgcolor=i_tablebgcol, text_color=i_panel_c_signal_text, text_halign=text.align_left,  text_size=tabletxtwi)

//◀─── Support/Resistance Lines Variables Declaration ───►

var int     numberofline        = i_maxline
var float   upperphzone         = na
var float   upperplzone         = na
var float   lowerphzone         = na
var float   lowerplzone         = na
var line[]  upperphzonearr      = array.new_line(0, na)
var line[]  upperplzonearr      = array.new_line(0, na)
var line[]  lowerphzonearr      = array.new_line(0, na)
var line[]  lowerplzonearr      = array.new_line(0, na)
var line    upperphzoneline     = na
var line    upperplzoneline     = na
var line    lowerphzoneline     = na
var line    lowerplzoneline     = na
var bool[]  upperzonetestedarr  = array.new_bool(0, false)
var bool[]  lowerzonetestedarr  = array.new_bool(0, false)
var bool    upperzonetested     = false
var bool    lowerzonetested     = false
var bool    nobool              = true
var bool    showprice           = true
var color   upperzonecolor      = i_rescol
var color   lowerzonecolor      = i_supcol
var label[] labelpharr          = array.new_label(0, na)
var label[] labelplarr          = array.new_label(0, na)
var label   labelph             = na
var label   labelpl             = na

//Resistance Lines Calculation

if i_showsr and shortcr
    upperphzone     := high_
    upperplzone     := close_ < open_     ? close_ : open_
    upperplzoneline := i_layout == 'Zone' ? line.new(bar_index_,  upperplzone, bar_index, upperplzone, width=i_linewidth) : na
    upperphzoneline := nobool             ? line.new(bar_index_,  upperphzone, bar_index, upperphzone, width=i_linewidth) : line.new(bar_index_, (upperphzone + upperplzone) / 2, bar_index, (upperphzone + upperplzone) / 2, width=i_linewidth)
    labelph         := showprice          ? label.new(bar_index_, nobool ? upperphzone : (upperphzone + upperplzone) / 2, text=str.tostring(math.round_to_mintick(bar_index - bar_index_)), textcolor=upperzonecolor, style=label.style_none) : na
    if array.size(upperphzonearr) > numberofline
        line.delete(array.shift(upperphzonearr))
        line.delete(array.shift(upperplzonearr))
        array.shift(upperzonetestedarr)
        label.delete(array.shift(labelpharr))
    array.push(upperphzonearr, upperphzoneline)
    array.push(upperplzonearr, upperplzoneline)
    array.push(upperzonetestedarr,       i_extend ? true : false)
    array.push(labelpharr,             labelph)
if array.size(upperplzonearr) > 0
    for i = 0 to array.size(upperplzonearr) - 1 by 1
        line  tempupperline  = array.get(upperphzonearr,     i)
        line  templowerline  = array.get(upperplzonearr,     i)
        label linepricelabel = array.get(labelpharr,         i)
        bool  tested         = array.get(upperzonetestedarr, i)
        line.set_style(tempupperline, f_i_linestyle(i_linestyle))
        line.set_style(templowerline, f_i_linestyle(i_linestyle))
        line.set_color(tempupperline, color.from_gradient(i,       1, numberofline, fzonecolor(upperzonecolor, 00), fzonecolor(upperzonecolor, 00)))
        line.set_color(templowerline, color.from_gradient(i,       1, numberofline, fzonecolor(upperzonecolor, 00), fzonecolor(upperzonecolor, 00)))
        label.set_textcolor(linepricelabel, color.from_gradient(i, 1, numberofline, fzonecolor(upperzonecolor, 00), upperzonecolor))
        label.set_text(linepricelabel, str.tostring(math.round_to_mintick(line.get_y1(tempupperline))))
        label.set_text(linepricelabel, '                                          Resistance - ' + str.tostring(math.round_to_mintick(line.get_y1(tempupperline))))
        label.set_x(linepricelabel, bar_index)
        crossed = high > line.get_y1(tempupperline)
        if crossed and not tested
            array.set(upperzonetestedarr, i,  true)
            label.delete(linepricelabel)
        else if i_extend ? tested : not tested
            line.set_x2(tempupperline, bar_index)
            array.set(upperphzonearr, i, tempupperline)
            line.set_x2(templowerline, bar_index)
            array.set(upperplzonearr, i, templowerline)

//Support Lines Calculation

if i_showsr and longcr
    lowerplzone     := low_
    lowerphzone     := close_ < open_     ? open_ : close_
    lowerphzoneline := i_layout == 'Zone' ? line.new(bar_index_,  lowerphzone, bar_index, lowerphzone, width=i_linewidth) : na
    lowerplzoneline := nobool             ? line.new(bar_index_,  lowerplzone, bar_index, lowerplzone, width=i_linewidth) : line.new(bar_index_, (lowerphzone + lowerplzone) / 2, bar_index, (lowerphzone + lowerplzone) / 2, width=i_linewidth)
    labelpl         := showprice          ? label.new(bar_index_, nobool ? lowerplzone : (lowerphzone + lowerplzone) / 2, text=str.tostring(math.round_to_mintick(bar_index - bar_index_)), textcolor=lowerzonecolor, style=label.style_none) : na
    if array.size(lowerphzonearr) > numberofline
        line.delete(array.shift(lowerphzonearr))
        line.delete(array.shift(lowerplzonearr))
        array.shift(lowerzonetestedarr)
        label.delete(array.shift(labelplarr))
    array.push(lowerphzonearr, lowerphzoneline)
    array.push(lowerplzonearr, lowerplzoneline)
    array.push(lowerzonetestedarr,       i_extend ? true : false)
    array.push(labelplarr,             labelpl)
if array.size(lowerplzonearr) > 0
    for i = 0 to array.size(lowerplzonearr) - 1 by 1
        line  tempupperline  = array.get(lowerphzonearr,     i)
        line  templowerline  = array.get(lowerplzonearr,     i)
        label linepricelabel = array.get(labelplarr,         i)
        bool  tested         = array.get(lowerzonetestedarr, i)
        line.set_style(tempupperline, f_i_linestyle(i_linestyle))
        line.set_style(templowerline, f_i_linestyle(i_linestyle))
        line.set_color(tempupperline, color.from_gradient(i,       1, numberofline, fzonecolor(lowerzonecolor, 00), fzonecolor(lowerzonecolor, 00)))
        line.set_color(templowerline, color.from_gradient(i,       1, numberofline, fzonecolor(lowerzonecolor, 00), fzonecolor(lowerzonecolor, 00)))
        label.set_textcolor(linepricelabel, color.from_gradient(i, 1, numberofline, fzonecolor(lowerzonecolor, 00), lowerzonecolor))
        label.set_text(linepricelabel, str.tostring(math.round_to_mintick(line.get_y1(templowerline))))
        label.set_text(linepricelabel, '                                     Support - ' + str.tostring(math.round_to_mintick(line.get_y1(templowerline))))
        label.set_x(linepricelabel, bar_index)
        crossed = low < line.get_y1(templowerline)
        if crossed and not tested
            array.set(lowerzonetestedarr, i, true)
            label.delete(linepricelabel)
        else if i_extend ? tested : not tested
            line.set_x2(tempupperline, bar_index)
            array.set(lowerphzonearr, i, tempupperline)
            line.set_x2(templowerline, bar_index)
            array.set(lowerplzonearr, i, templowerline)

//◀─── Psychological Levels ───►

//Constant Variable

var incr = syminfo.type == 'cfd' ? syminfo.mintick * 5000 : syminfo.type == 'crypto' ? syminfo.mintick * 5000 : syminfo.mintick * 500

//Calculation

if i_psylevels and barstate.islast
    for counter  = 0 to i_linescounter - 1 by 1
        incrup   = math.ceil(close  / incr) * incr + counter * incr
        incrdown = math.floor(close / incr) * incr - counter * incr

//Plotting

        line.new(bar_index, incrup,   bar_index - 1, incrup,   xloc=xloc.bar_index, extend=extend.both, color=i_psylinescol, width=1, style=line.style_solid)
        line.new(bar_index, incrdown, bar_index - 1, incrdown, xloc=xloc.bar_index, extend=extend.both, color=i_psylinescol, width=1, style=line.style_solid)

//◀─── Alerts ───►

if sell ? high + atrxy          : na
    alert('Sell Condition! At '              + str.tostring(math.round_to_mintick(close)), alert.freq_once_per_bar)
if buy  ? low  - atrxy          : na
    alert('Buy Condition! At '               + str.tostring(math.round_to_mintick(close)), alert.freq_once_per_bar)
if shortcr     ? (high) + atrxy : na
    alert('Sell Continuity/Reversal! At '    + str.tostring(math.round_to_mintick(close)), alert.freq_once_per_bar)
if longcr      ? (low)  - atrxy : na
    alert('Buy Continuity/Reversal! At '     + str.tostring(math.round_to_mintick(close)), alert.freq_once_per_bar)
if i_showkjhlabels or i_showkjhsr and hiddenbearkj == 1
    alert('Hidden Kijun Resistance! At '     + str.tostring(math.round_to_mintick(close)), alert.freq_once_per_bar)
if i_showkjhlabels or i_showkjhsr and hiddenbullkj == 1
    alert('Hidden Kijun Support! At '        + str.tostring(math.round_to_mintick(close)), alert.freq_once_per_bar)
if table_downtrend
    alert('Panel : Bearish Trend! At '       + str.tostring(math.round_to_mintick(close)), alert.freq_once_per_bar)
if table_uptrend
    alert('Panel : Bullish Trend! At '       + str.tostring(math.round_to_mintick(close)), alert.freq_once_per_bar)
if table_consolidation
    alert('Panel : Trend Consolidation! At ' + str.tostring(math.round_to_mintick(close)), alert.freq_once_per_bar)

