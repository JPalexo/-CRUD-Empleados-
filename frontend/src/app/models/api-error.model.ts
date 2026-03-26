export interface ApiError {
  status: number;
  code?: string;
  message: string;
  fieldErrors?: Record<string, string[]>;
  timestamp?: string;
}
