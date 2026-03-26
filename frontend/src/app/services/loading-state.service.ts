import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingStateService {
  private readonly activeRequests = signal(0);
  readonly isLoading = signal(false);

  start(): void {
    this.activeRequests.set(this.activeRequests() + 1);
    this.isLoading.set(this.activeRequests() > 0);
  }

  stop(): void {
    this.activeRequests.set(Math.max(0, this.activeRequests() - 1));
    this.isLoading.set(this.activeRequests() > 0);
  }
}
