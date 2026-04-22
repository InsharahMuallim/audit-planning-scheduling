"""
app.py — Flask Entry Point with Rate Limiting
Tool-21: Audit Planning and Scheduling
AI Developer 3 — Day 4 Task
"""

from flask import Flask, jsonify, request
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from routes.sanitisation import sanitise_input

app = Flask(__name__)


# ─────────────────────────────────────────────
# 1. SETUP FLASK-LIMITER
# Default limit: 30 requests per minute per IP
# ─────────────────────────────────────────────
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    storage_uri="memory://",
)


# ─────────────────────────────────────────────
# 2. CUSTOM ERROR HANDLER FOR 429
# Returns clear error message with retry_after
# ─────────────────────────────────────────────
@app.errorhandler(429)
def rate_limit_exceeded(e):
    return jsonify({
        "error": "Too Many Requests",
        "message": "You have exceeded the allowed request limit. Please slow down.",
        "retry_after": 60,
        "retry_after_unit": "seconds",
        "status": 429
    }), 429


# ─────────────────────────────────────────────
# 3. HEALTH CHECK ENDPOINT
# No rate limit on health check
# ─────────────────────────────────────────────
@app.route("/health", methods=["GET"])
@limiter.exempt
def health():
    return jsonify({
        "status": "ok",
        "message": "AI service is running",
        "rate_limits": {
            "default": "30 requests per minute",
            "generate_report": "10 requests per minute"
        }
    }), 200


# ─────────────────────────────────────────────
# 4. DESCRIBE ENDPOINT — 30 req/min
# ─────────────────────────────────────────────
@app.route("/describe", methods=["POST"])
@sanitise_input
def describe():
    clean_body = request.sanitised_body
    return jsonify({"message": "Describe endpoint working!", "received": clean_body, "status": 200}), 200


# ─────────────────────────────────────────────
# 5. RECOMMEND ENDPOINT — 30 req/min
# ─────────────────────────────────────────────
@app.route("/recommend", methods=["POST"])
@sanitise_input
def recommend():
    clean_body = request.sanitised_body
    return jsonify({"message": "Recommend endpoint working!", "received": clean_body, "status": 200}), 200


# ─────────────────────────────────────────────
# 6. GENERATE REPORT — Strict 10 req/min
# ─────────────────────────────────────────────
@app.route("/generate-report", methods=["POST"])
@limiter.limit("10 per minute")
@sanitise_input
def generate_report():
    clean_body = request.sanitised_body
    return jsonify({"message": "Generate report endpoint working!", "received": clean_body, "status": 200}), 200


# ─────────────────────────────────────────────
# 7. CATEGORISE ENDPOINT — 30 req/min
# ─────────────────────────────────────────────
@app.route("/categorise", methods=["POST"])
@sanitise_input
def categorise():
    clean_body = request.sanitised_body
    return jsonify({"message": "Categorise endpoint working!", "received": clean_body, "status": 200}), 200


# ─────────────────────────────────────────────
# RUN THE APP
# ─────────────────────────────────────────────
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
