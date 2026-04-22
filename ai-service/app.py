"""
app.py — Flask Entry Point
Tool-21: Audit Planning and Scheduling
AI Developer 3 — Day 3 Task
"""

from flask import Flask, jsonify, request
from routes.sanitisation import sanitise_input

app = Flask(__name__)


# ─────────────────────────────────────────────
# HEALTH CHECK ENDPOINT
# ─────────────────────────────────────────────
@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "message": "AI service is running"}), 200


# ─────────────────────────────────────────────
# TEST ENDPOINT — Tests sanitisation middleware
# ─────────────────────────────────────────────
@app.route("/test-sanitise", methods=["POST"])
@sanitise_input
def test_sanitise():
    """
    Test endpoint to verify sanitisation is working.
    Send a POST request with JSON body to test.
    """
    clean_body = request.sanitised_body
    return jsonify({
        "message": "Input is clean and safe!",
        "received": clean_body,
        "status": 200
    }), 200


# ─────────────────────────────────────────────
# RUN THE APP
# ─────────────────────────────────────────────
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
