# -*- coding: utf-8 -*-
import re, html, json, os

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MD = os.path.join(BASE, 'stage1', 'final_draft.md')
OUT = os.path.join(BASE, 'stage2', 'formatted-臻冰柱与寒霜状态系统策划案.html')
TOKENS = os.path.join(BASE, 'stage2', 'design_tokens.json')

lines = open(MD, encoding='utf-8').read().split('\n')

def inline(s):
    s = html.escape(s, quote=False)
    s = re.sub(r'`([^`]+)`', r'<code>\1</code>', s)
    s = re.sub(r'\*\*([^*]+)\*\*', r'<strong>\1</strong>', s)
    s = re.sub(r'(?<!\*)\*([^*]+)\*(?!\*)', r'<em>\1</em>', s)
    return s

blocks = []
i = 0
while i < len(lines):
    st = lines[i].strip()
    if not st:
        i += 1; continue
    m = re.match(r'^(#{1,3})\s+(.*)$', st)
    if m:
        blocks.append({'t':'h','lv':len(m.group(1)),'txt':m.group(2).strip()}); i += 1; continue
    if st.startswith('```'):
        i += 1; code=[]
        while i < len(lines) and not lines[i].strip().startswith('```'):
            code.append(lines[i]); i += 1
        i += 1
        blocks.append({'t':'code','txt':'\n'.join(code)}); continue
    if st.startswith('>'):
        quote=[]
        while i < len(lines) and lines[i].strip().startswith('>'):
            quote.append(lines[i].strip().lstrip('>').strip()); i += 1
        blocks.append({'t':'quote','txt':' '.join(quote)}); continue
    if st.startswith('|') and i+1 < len(lines) and re.match(r'^\|[\s:|-]+\|$', lines[i+1].strip()):
        header=[c.strip() for c in st.strip('|').split('|')]
        i += 2; rows=[]
        while i < len(lines) and lines[i].strip().startswith('|'):
            rows.append([c.strip() for c in lines[i].strip().strip('|').split('|')]); i += 1
        blocks.append({'t':'table','head':header,'rows':rows}); continue
    if re.match(r'^(\d+\.|-)\s+', st):
        ordered = bool(re.match(r'^\d+\.\s+', st)); items=[]
        while i < len(lines):
            mm=re.match(r'^(\d+\.|-)\s+(.*)$', lines[i].strip())
            if not mm: break
            items.append(mm.group(2).strip()); i += 1
        blocks.append({'t':'list','ordered':ordered,'items':items}); continue
    para=[st]; i += 1
    while i < len(lines):
        s2=lines[i].strip()
        if not s2: break
        if re.match(r'^#{1,3}\s+', s2) or s2.startswith('```') or s2.startswith('>') or s2.startswith('|') or re.match(r'^(\d+\.|-)\s+', s2): break
        para.append(s2); i += 1
    blocks.append({'t':'p','txt':' '.join(para)})

# 锚点
sec_id={}; cnt=0
for b in blocks:
    if b['t']=='h' and b['lv']<=2:
        cnt+=1; sec_id[id(b)]=f'section-{cnt}'

# h2 后紧跟 h3 时插入导语（TQ-03）
render_blocks_list=[]
for idx,b in enumerate(blocks):
    render_blocks_list.append(b)
    if b['t']=='h' and b['lv']==2:
        nxt = blocks[idx+1] if idx+1 < len(blocks) else None
        if nxt and nxt['t']=='h' and nxt['lv']==3:
            render_blocks_list.append({'t':'lead','txt':'本节按子项分述如下。'})

def render_blocks(blks):
    out=[]
    for b in blks:
        if b['t']=='h':
            lv=b['lv']; txt=inline(b['txt']); sid=sec_id.get(id(b),'')
            anchor=f' id="{sid}"' if sid else ''
            if lv==1:
                out.append(f'<h1{anchor} class="sec-h1">{txt}</h1>')
            elif lv==2:
                out.append(f'<h2{anchor} class="sec-h2">&nbsp;&nbsp;{txt}</h2>')
            else:
                out.append(f'<h3{anchor} class="sec-h3">{txt}</h3>')
        elif b['t']=='p':
            out.append(f'<p class="body-p">{inline(b["txt"])}</p>')
        elif b['t']=='lead':
            out.append(f'<p class="lead-p">{inline(b["txt"])}</p>')
        elif b['t']=='list':
            tag='ol' if b['ordered'] else 'ul'
            lis=''.join(f'<li class="li-item">{inline(x)}</li>' for x in b['items'])
            out.append(f'<{tag} class="list-block">{lis}</{tag}>')
        elif b['t']=='table':
            head=''.join(f'<th class="th-cell">{inline(c)}</th>' for c in b['head'])
            rows=[]
            for r in b['rows']:
                cells=''.join(f'<td class="td-cell">{inline(c)}</td>' for c in r)
                rows.append(f'<tr>{cells}</tr>')
            out.append(f'<table class="data-table"><thead><tr>{head}</tr></thead><tbody>{"".join(rows)}</tbody></table>')
        elif b['t']=='quote':
            out.append(f'<p class="quote-p">&nbsp;&nbsp;{inline(b["txt"])}</p>')
        elif b['t']=='code':
            out.append(f'<pre class="code-block">{html.escape(b["txt"])}</pre>')
    return '\n'.join(out)

title='臻冰柱与寒霜状态系统策划案'
category='游戏系统设计文档'
subtitle='寒霜状态链 · 臻冰柱场景 · 归煦逆转机制'
date='2026-11-17'
version='v1.0'

toc_items=[]
for b in blocks:
    if b['t']=='h' and b['lv']==2:
        sid=sec_id.get(id(b),'')
        toc_items.append(f'<li class="toc-item"><a class="toc-link" href="#{sid}">{inline(b["txt"])}</a></li>')

body_html=render_blocks(render_blocks_list)

style='''
  @page { @bottom-center { content: counter(page); } }
  @page cover { @bottom-center { content: none; } }
  section[role="cover"] { page: cover; }
  :root {
    --ff-heading: "宋体", SimSun, serif;
    --ff-body: "宋体", SimSun, serif;
    --ff-code: Consolas, "Courier New", monospace;
    --fs-cover-cat: 14pt;
    --fs-cover-title: 26pt;
    --fs-cover-sub: 13pt;
    --fs-cover-meta: 11pt;
    --fs-toc-title: 16pt;
    --fs-h1: 16pt;
    --fs-h2: 14pt;
    --fs-h3: 12pt;
    --fs-body: 12pt;
    --fs-small: 10.5pt;
    --lh-body: 1.6;
    --color-primary: #1565C0;
    --color-secondary: #0288D1;
    --color-accent: #00897B;
    --color-heading: #1A237E;
    --color-text: #212121;
    --color-muted: #616161;
    --color-border: #E0E0E0;
    --color-surface: #F5F7FA;
    --color-white: #FFFFFF;
    --sp-xs: 0.3em;
    --sp-sm: 0.6em;
    --sp-md: 0.75em;
    --sp-lg: 1.6em;
    --pad-cell-v: 0.4em;
    --pad-cell-h: 0.6em;
    --pad-block: 0.8em;
    --indent-body: 2em;
    --border-thin: 1px;
    --border-accent: 4px;
    --page-content-width: 15.6cm;
    --cover-gap-top: 5cm;
    --cover-gap-bottom: 3cm;
  }
  * { box-sizing: border-box; }
  body { font-family: var(--ff-body); font-size: var(--fs-body); line-height: var(--lh-body); color: var(--color-text); background: var(--color-white); max-width: var(--page-content-width); margin-left: auto; margin-right: auto; }
  .cover-category { text-align: center; font-family: var(--ff-heading); font-size: var(--fs-cover-cat); font-weight: 700; color: var(--color-primary); letter-spacing: 0.08em; margin-top: var(--cover-gap-top); margin-bottom: var(--sp-sm); }
  .cover-title { text-align: center; font-family: var(--ff-heading); font-size: var(--fs-cover-title); font-weight: 700; color: var(--color-heading); line-height: var(--lh-body); margin-bottom: var(--sp-lg); }
  .cover-subtitle { text-align: center; font-family: var(--ff-body); font-size: var(--fs-cover-sub); color: var(--color-muted); margin-bottom: var(--cover-gap-bottom); }
  .cover-meta-line { text-align: center; font-family: var(--ff-body); font-size: var(--fs-cover-meta); color: var(--color-muted); text-indent: 0; line-height: 2; }
  .toc-title { text-align: center; font-family: var(--ff-heading); font-size: var(--fs-toc-title); font-weight: 700; color: var(--color-heading); margin-bottom: var(--sp-lg); }
  .toc-list { padding-left: 0; margin: 0; }
  .toc-item { text-indent: 0; margin-bottom: var(--sp-xs); }
  .toc-link { color: var(--color-primary); text-decoration: none; font-family: var(--ff-body); font-size: var(--fs-body); }
  .executive-summary { border-left: var(--border-accent) solid var(--color-primary); background: var(--color-surface); padding: var(--pad-block); margin-bottom: var(--sp-lg); text-indent: 0; }
  .executive-title { font-family: var(--ff-heading); font-size: var(--fs-h2); font-weight: 700; color: var(--color-heading); text-indent: 0; margin-bottom: var(--sp-sm); }
  .executive-body { font-family: var(--ff-body); font-size: var(--fs-body); line-height: var(--lh-body); text-indent: 2em; }
  .sec-h1 { font-family: var(--ff-heading); font-size: var(--fs-h1); color: var(--color-white); background: var(--color-primary); padding: var(--sp-sm); margin-bottom: var(--sp-md); }
  .sec-h2 { font-family: var(--ff-heading); font-size: var(--fs-h2); color: var(--color-heading); border-left: var(--border-accent) solid var(--color-secondary); padding-left: var(--sp-sm); margin-top: var(--sp-lg); margin-bottom: var(--sp-sm); }
  .sec-h3 { font-family: var(--ff-heading); font-size: var(--fs-h3); color: var(--color-primary); margin-top: var(--sp-md); margin-bottom: var(--sp-xs); }
  .body-p { font-family: var(--ff-body); font-size: var(--fs-body); line-height: var(--lh-body); text-indent: var(--indent-body); text-align: justify; margin-bottom: var(--sp-md); }
  .lead-p { font-family: var(--ff-body); font-size: var(--fs-body); line-height: var(--lh-body); text-indent: var(--indent-body); color: var(--color-muted); margin-bottom: var(--sp-md); }
  .list-block { margin-bottom: var(--sp-md); padding-left: var(--indent-body); }
  .li-item { font-family: var(--ff-body); font-size: var(--fs-body); line-height: var(--lh-body); margin-bottom: var(--sp-xs); text-indent: 0; }
  .data-table { width: 100%; border-collapse: collapse; margin: var(--sp-sm) 0 var(--sp-lg) 0; }
  .th-cell { font-family: var(--ff-heading); font-size: var(--fs-small); background: var(--color-surface); color: var(--color-heading); border: var(--border-thin) solid var(--color-border); padding: var(--pad-cell-v) var(--pad-cell-h); text-align: left; }
  .td-cell { font-family: var(--ff-body); font-size: var(--fs-small); border: var(--border-thin) solid var(--color-border); padding: var(--pad-cell-v) var(--pad-cell-h); text-indent: 0; vertical-align: top; }
  .quote-p { font-family: var(--ff-body); font-size: var(--fs-body); line-height: var(--lh-body); border-left: var(--border-accent) solid var(--color-accent); background: var(--color-surface); padding: var(--pad-block); text-indent: 0; margin: var(--sp-sm) 0; }
  .code-block { font-family: var(--ff-code); font-size: var(--fs-small); background: var(--color-surface); border: var(--border-thin) solid var(--color-border); padding: var(--pad-block); white-space: pre-wrap; text-indent: 0; margin: var(--sp-sm) 0 var(--sp-lg) 0; }
  code { font-family: var(--ff-code); font-size: var(--fs-small); background: var(--color-surface); }
'''

summary='''<div class="executive-summary">
  <p class="executive-title">执行摘要</p>
  <p class="executive-body">本策划案定义「臻冰柱 → 寒冷 → 落霜 → 冻结」的单线互斥负面状态链，以及由「西岭青 / 归煦」驱动的「温暖 / 化霜 / 消融」正面逆转链。臻冰柱在范围内熄火并施加寒冷，3 秒后转为落霜；臻冰棒可将落霜推进为冻结并击碎冰柱造成范围控制；西岭青则在获得瞬间转化已有负面并开启免疫。玩家与生物遵循同一套状态规则。</p>
</div>'''

html_doc=f'''<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="docx-page-size" content="A4">
<title>{title}</title>
<style>{style}</style>
</head>
<body>
<section role="cover">
  <p class="cover-category">{category}</p>
  <p class="cover-title">{title}</p>
  <p class="cover-subtitle">{subtitle}</p>
  <p class="cover-meta-line">版本：{version}</p>
  <p class="cover-meta-line">日期：{date}</p>
</section>
<section role="cover">
  <p class="toc-title">目录</p>
  <ol class="toc-list">{''.join(toc_items)}</ol>
</section>
<section role="body" data-page-restart="1">
  {summary}
  {body_html}
</section>
</body>
</html>
'''

open(OUT,'w',encoding='utf-8').write(html_doc)
tokens_obj={"genre":"business-report","theme_name":"business-modern","source":"design-token/tokens/compiled/business-report.json","note":"§2.5 硬约束覆盖：字体首项 SimSun、正文12pt、text-indent:2em；样式全 class 化 + var()"}
open(TOKENS,'w',encoding='utf-8').write(json.dumps(tokens_obj,ensure_ascii=False,indent=2))
print('HTML written:', OUT)
print('size:', len(html_doc), 'blocks:', len(blocks))
