export function httpErrorMessage(error: unknown, fallback = 'Ocurrio un error inesperado.'): string {
  if (!error || typeof error !== 'object') {
    return fallback;
  }

  const maybeError = error as {
    status?: number;
    error?: { message?: string };
    message?: string;
  };

  if (maybeError.error?.message) {
    return maybeError.error.message;
  }

  if (maybeError.message) {
    return maybeError.message;
  }

  if (maybeError.status === 0) {
    return 'No fue posible conectar con el backend.';
  }

  return fallback;
}
