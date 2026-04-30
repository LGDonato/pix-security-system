from pathlib import Path

import pandas as pd


PROJECT_ROOT = Path(__file__).resolve().parents[1]
INPUT_FILE = PROJECT_ROOT / "data" / "silver" / "pix_transactions_silver.parquet"
OUTPUT_DIR = PROJECT_ROOT / "data" / "gold"

CUSTOMER_METRICS_FILE = OUTPUT_DIR / "customer_metrics.parquet"
RISK_DISTRIBUTION_FILE = OUTPUT_DIR / "risk_distribution.parquet"
NIGHT_TRANSACTIONS_FILE = OUTPUT_DIR / "night_transactions.parquet"
DEVICE_USAGE_FILE = OUTPUT_DIR / "device_usage.parquet"

REQUIRED_COLUMNS = [
    "customerId",
    "amountReais",
    "riskCategory",
    "isNightTransaction",
    "deviceId",
]


def validate_required_columns(dataframe):
    missing_columns = [column for column in REQUIRED_COLUMNS if column not in dataframe.columns]

    if missing_columns:
        raise ValueError(f"Missing required columns in Silver data: {', '.join(missing_columns)}")


def build_customer_metrics(dataframe):
    return (
        dataframe.groupby("customerId", as_index=False)
        .agg(
            total_transactions=("customerId", "count"),
            total_amount=("amountReais", "sum"),
            avg_amount=("amountReais", "mean"),
        )
        .sort_values("customerId")
    )


def build_risk_distribution(dataframe):
    risk_distribution = (
        dataframe.groupby("riskCategory", as_index=False)
        .size()
        .rename(columns={"size": "count"})
    )
    total_records = len(dataframe)
    risk_distribution["percentage"] = (risk_distribution["count"] / total_records) * 100
    return risk_distribution.sort_values("riskCategory")


def build_night_transactions(dataframe):
    return (
        dataframe.groupby("isNightTransaction", as_index=False)
        .size()
        .rename(columns={"size": "count"})
        .sort_values("isNightTransaction")
    )


def build_device_usage(dataframe):
    return (
        dataframe.groupby("deviceId", as_index=False)
        .size()
        .rename(columns={"size": "count"})
        .sort_values("deviceId")
    )


def process_gold():
    # Gold stores analytical aggregates ready for dashboards and reporting.
    if not INPUT_FILE.exists():
        raise FileNotFoundError(
            f"Input file not found: {INPUT_FILE}. Run data_pipeline/silver.py first."
        )

    dataframe = pd.read_parquet(INPUT_FILE)
    validate_required_columns(dataframe)

    customer_metrics = build_customer_metrics(dataframe)
    risk_distribution = build_risk_distribution(dataframe)
    night_transactions = build_night_transactions(dataframe)
    device_usage = build_device_usage(dataframe)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    customer_metrics.to_parquet(CUSTOMER_METRICS_FILE, index=False)
    risk_distribution.to_parquet(RISK_DISTRIBUTION_FILE, index=False)
    night_transactions.to_parquet(NIGHT_TRANSACTIONS_FILE, index=False)
    device_usage.to_parquet(DEVICE_USAGE_FILE, index=False)

    print(f"Total records read: {len(dataframe)}")
    print(f"Customers: {dataframe['customerId'].nunique()}")
    print("Risk distribution:")
    print(risk_distribution.to_string(index=False))


def main():
    process_gold()


if __name__ == "__main__":
    main()
