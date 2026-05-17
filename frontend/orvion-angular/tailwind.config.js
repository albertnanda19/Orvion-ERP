/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        primary: { 50: '#eff6ff', 100: '#dbeafe', 200: '#bfdbfe', 300: '#93c5fd', 400: '#60a5fa', 500: '#1a56db', 600: '#1e429f', 700: '#1e3a8a', 800: '#1e3a5f', 900: '#0f172a' },
        orvion: { dark: '#0f172a', medium: '#1e293b', light: '#334155', surface: '#f8fafc' }
      },
      fontFamily: { sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'] }
    }
  },
  plugins: [],
  important: true,
  corePlugins: { preflight: false }
};
