import { defineConfig } from '@playwright/test'

export default defineConfig({
  testDir: './tests/smoke',
  fullyParallel: false,
  workers: 1,
  reporter: [['list'], ['html', { outputFolder: 'test-results/report', open: 'never' }]],
  use: {
    baseURL: 'http://127.0.0.1:15173',
    browserName: 'chromium',
    channel: 'chrome',
    headless: true,
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure'
  },
  outputDir: 'test-results/artifacts',
  timeout: 30_000,
  expect: { timeout: 8_000 }
})
