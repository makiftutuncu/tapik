import { defineConfig, devices } from '@playwright/test'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const repository = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..')

export default defineConfig({
  testDir: './tests',
  outputDir: './target/browser-results',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  workers: 2,
  retries: 0,
  reporter: [['list'], ['html', { outputFolder: path.join(repository, 'site/target/browser-report'), open: 'never' }]],
  use: {
    baseURL: 'http://127.0.0.1:4173',
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure'
  },
  projects: [
    { name: 'desktop', use: { ...devices['Desktop Chrome'], viewport: { width: 1440, height: 1000 } } },
    { name: 'mobile', use: { ...devices['Pixel 7'], viewport: { width: 390, height: 844 } } }
  ],
  webServer: {
    command: 'node site/scripts/serve-documentation.mjs',
    cwd: repository,
    url: 'http://127.0.0.1:4173',
    reuseExistingServer: false
  }
})
