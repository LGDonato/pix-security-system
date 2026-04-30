import argparse
import json
import random
import uuid
from datetime import datetime, timedelta, timezone
from pathlib import Path


OUTPUT_FILE = Path(__file__).resolve().parents[1] / "data" / "events" / "pix_transactions.jsonl"

CUSTOMER_IDS = [
    "customer-1001",
    "customer-1002",
    "customer-1003",
    "customer-1004",
    "customer-1005",
]

TRUSTED_DEVICES = [
    "device-trusted-001",
    "device-trusted-002",
    "device-trusted-003",
    "device-trusted-004",
]

UNKNOWN_DEVICES = [
    "device-unknown-901",
    "device-unknown-902",
    "device-unknown-903",
    "device-unknown-904",
]

TRANSACTION_TYPES = ["PIX_TRANSFER", "PIX_PAYMENT", "PIX_REFUND"]
CHANNELS = ["MOBILE_APP", "WEB", "ATM"]


def parse_args():
    parser = argparse.ArgumentParser(
        description="Generate simulated PIX transaction events in JSON Lines format."
    )
    parser.add_argument(
        "--count",
        type=int,
        default=100,
        help="Number of events to generate. Defaults to 100.",
    )
    return parser.parse_args()


def random_amount_cents():
    ranges = [
        (500, 10_000),
        (10_001, 100_000),
        (100_001, 1_000_000),
        (1_000_001, 5_000_000),
    ]
    start, end = random.choice(ranges)
    return random.randint(start, end)


def random_transaction_datetime():
    base_date = datetime.now(timezone.utc) - timedelta(days=random.randint(0, 30))
    hour = random.choice([random.randint(8, 20), random.choice([0, 1, 2, 3, 4, 22, 23])])
    return base_date.replace(
        hour=hour,
        minute=random.randint(0, 59),
        second=random.randint(0, 59),
        microsecond=0,
    ).isoformat()


def generate_event():
    # This script simulates a PIX event source that could be replaced by Kafka later.
    device_pool = TRUSTED_DEVICES if random.random() < 0.7 else UNKNOWN_DEVICES

    return {
        "transactionId": str(uuid.uuid4()),
        "customerId": random.choice(CUSTOMER_IDS),
        "amountCents": random_amount_cents(),
        "deviceId": random.choice(device_pool),
        "transactionDateTime": random_transaction_datetime(),
        "transactionType": random.choice(TRANSACTION_TYPES),
        "channel": random.choice(CHANNELS),
    }


def write_events(count):
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)

    with OUTPUT_FILE.open("w", encoding="utf-8") as file:
        for _ in range(count):
            event = generate_event()
            file.write(json.dumps(event, separators=(",", ":")) + "\n")


def main():
    args = parse_args()

    if args.count < 0:
        raise ValueError("--count must be zero or greater.")

    write_events(args.count)
    print(f"Generated {args.count} PIX events at {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
