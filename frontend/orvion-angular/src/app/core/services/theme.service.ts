import { Injectable, signal, effect } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  isDarkMode = signal(this.loadPreference());

  constructor() {
    effect(() => {
      const dark = this.isDarkMode();
      localStorage.setItem('orvion-theme', dark ? 'dark' : 'light');
      document.body.classList.toggle('dark', dark);
    });
  }

  toggleTheme(): void { this.isDarkMode.update(v => !v); }

  private loadPreference(): boolean {
    const stored = localStorage.getItem('orvion-theme');
    return stored === 'dark' || (!stored && window.matchMedia('(prefers-color-scheme: dark)').matches);
  }
}
