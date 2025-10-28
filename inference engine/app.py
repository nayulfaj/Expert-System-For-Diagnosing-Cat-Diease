
from flask import Flask, render_template, request, redirect, url_for
import json, os

BASE = os.path.dirname(__file__)
def load_rules():
    with open(os.path.join(BASE,"rules.json"),"r",encoding="utf-8") as f:
        return json.load(f)
def load_symptoms():
    with open(os.path.join(BASE,"symptoms.json"),"r",encoding="utf-8") as f:
        return json.load(f)
def load_diseases():
    with open(os.path.join(BASE,"diseases.json"),"r",encoding="utf-8") as f:
        return json.load(f)

def combine_cf(cf1, cf2):
    if cf1 is None:
        return cf2
    if cf2 is None:
        return cf1
    if cf1 >= 0 and cf2 >= 0:
        return cf1 + cf2*(1 - cf1)
    if cf1 < 0 and cf2 < 0:
        return cf1 + cf2*(1 + cf1)
    return (cf1 + cf2) / (1 - min(abs(cf1), abs(cf2)))

def eval_rule(rule, facts_cf):
    antecedents = rule["if"]
    cfs = []
    for a in antecedents:
        cfs.append(facts_cf.get(a, None))
    combined = None
    for c in cfs:
        if c is not None:
            if combined is None:
                combined = c
            else:
                combined = combine_cf(combined, c)
    if combined is None:
        return None
    rule_cf = rule.get("cf",1.0)
    return combined * rule_cf

def forward_chain_and_cf(rules, initial_facts_cf):
    facts_cf = dict(initial_facts_cf)
    applied = set()
    changed = True
    while changed:
        changed = False
        for rule in rules:
            rid = rule.get("id")
            if rid in applied:
                continue
            val = eval_rule(rule, facts_cf)
            if val is None:
                continue
            conclusion = rule["then"]
            existing = facts_cf.get(conclusion, None)
            new_combined = combine_cf(existing, val) if existing is not None else val
            if existing is None or abs(new_combined - existing) > 1e-6:
                facts_cf[conclusion] = new_combined
            applied.add(rid)
            changed = True
    return facts_cf

app = Flask(__name__)
rules = load_rules()
symptoms = load_symptoms()
diseases = load_diseases()

CONF_MAP = {
    "sangat_yakin": 1.0,
    "yakin": 0.8,
    "cukup_yakin": 0.6,
    "sedikit_yakin": 0.4,
    "kurang_yakin": 0.2,
    "tidak_yakin": 0.0
}

@app.route("/", methods=["GET","POST"])
def index():
    if request.method == "POST":
        facts_cf = {}
       
        for i in range(1,7):
            sym = request.form.get(f"sym_{i}", "none")
            conf = request.form.get(f"conf_{i}", "cukup_yakin")
            if sym and sym != "none":
                facts_cf[sym] = CONF_MAP.get(conf, 0.6)
            
        result_facts = forward_chain_and_cf(rules, facts_cf)
        disease_results = []
        for dcode, dname in diseases.items():
            cf_val = result_facts.get(dcode, None)
            if cf_val is not None:
                disease_results.append((dcode, dname, cf_val))
        disease_results.sort(key=lambda x: x[2], reverse=True)
        disease_results = [(c,d,round(cf,2)) for (c,d,cf) in disease_results]
        return render_template("results.html", results=disease_results)
    # GET
    # To keep dropdowns concise, pass symptoms dict to template
    return render_template("index_dropdowns.html", symptoms=symptoms)
    
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
