
Expert System Demo (Flask)
Files created:
- rules.json : knowledge base with rules (includes parallel and sequential examples)
- symptoms.json : symptom labels
- diseases.json : disease labels
- app.py : Flask application
- templates/ : HTML templates (index.html, results.html)

How to run locally:
1. Install Flask: pip install flask
2. cd /mnt/data/expert_system
3. python app.py
4. Open http://127.0.0.1:5000 in your browser

Notes for the assignment:
- The rules.json is based on "Application Of Expert System For Diagnosing Cat Disease..." (uploaded PDF).
- I added R9/R10 to demonstrate parallel rules producing the same conclusion (P06) with different CFs.
- R11 demonstrates a sequential rule using a derived fact (P06) as premise.
- The CF combination follows standard formulas (positive/negative/different signs).
- For a proper UI for your course, you can change the form to send only selected symptoms (currently sends all with a choice).
