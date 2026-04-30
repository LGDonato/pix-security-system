from pathlib import Path

import pandas as pd


PROJECT_ROOT = Path(__file__).resolve().parents[1]
INPUT_FILE = PROJECT_ROOT / "data" / "bronze" / "pix_transactions.parquet"
OUTPUT_FILE = PROJECT_ROOT / "data" / "silver" / "pix_transactions_silver.parquet"

REQUIRED_COLUMNS = [
    "transactionId",
    "customerId",
    "amountCents",
    "deviceId",
    "transactionDateTime",
    "transactionType",
    "channel",
]


def validate_required_columns(dataframe):
    missing_columns = [column for column in REQUIRED_COLUMNS if column not in dataframe.columns]

    if missing_columns:
        raise ValueError(f"Missing required columns in Bronze data: {', '.join(missing_columns)}")


def add_risk_category(dataframe):
    dataframe["riskCategory"] = "LOW"
    dataframe.loc[
        (dataframe["amountCents"] > 100_000) & (dataframe["amountCents"] <= 300_000),
        "riskCategory",
    ] = "MEDIUM"
    dataframe.loc[dataframe["amountCents"] > 300_000, "riskCategory"] = "HIGH"


def process_silver():
    # Silver stores treated and enriched data derived from the raw Bronze layer.
    if not INPUT_FILE.exists():
        raise FileNotFoundError(
            f"Input file not found: {INPUT_FILE}. Run data_pipeline/bronze.py first."
        )

    dataframe = pd.read_parquet(INPUT_FILE, engine="pyarrow")
    records_read = len(dataframe)

    validate_required_columns(dataframe)

    dataframe = dataframe.dropna(subset=REQUIRED_COLUMNS).copy()
    dataframe["amountCents"] = pd.to_numeric(dataframe["amountCents"], errors="coerce")
    dataframe["transactionDateTime"] = pd.to_datetime(
        dataframe["transactionDateTime"],
        errors="coerce",
        utc=True,
    )
    dataframe = dataframe.dropna(subset=["amountCents", "transactionDateTime"])
    dataframe = dataframe[dataframe["amountCents"] > 0].copy()

    dataframe["amountReais"] = dataframe["amountCents"] / 100
    dataframe["transactionHour"] = dataframe["transactionDateTime"].dt.hour
    dataframe["isNightTransaction"] = (
        (dataframe["transactionHour"] >= 20) | (dataframe["transactionHour"] < 6)
    )
    add_risk_category(dataframe)

    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True)
    dataframe.to_parquet(OUTPUT_FILE, engine="pyarrow", index=False)

    records_saved = len(dataframe)
    records_removed = records_read - records_saved
    print(f"Records read: {records_read}")
    print(f"Records removed: {records_removed}")
    print(f"Records saved: {records_saved}")
    print(f"Silver data saved at {OUTPUT_FILE}")


def main():
    process_silver()


if __name__ == "__main__":
    main()
