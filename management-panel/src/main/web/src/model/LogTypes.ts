export type Log = {
    id: number;
    records: string[]
}

export type LogRecordRequest = {
    logId: number,
    record: string
}