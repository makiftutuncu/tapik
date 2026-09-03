import { expect, test } from '@playwright/test'
import { readFileSync } from 'node:fs'

const currentVersion = readFileSync(new URL('../../docs/antora.yml', import.meta.url), 'utf8').match(/^version:\s*(\S+)/m)[1]

test.beforeEach(async ({ page }) => {
  page.documentationErrors = []
  page.on('pageerror', error => page.documentationErrors.push(error.message))
  page.on('response', response => {
    if (response.url().startsWith('http://127.0.0.1:4173/') && response.status() >= 400) {
      page.documentationErrors.push(`${response.status()} ${response.url()}`)
    }
  })
})

test.afterEach(async ({ page }) => {
  expect(page.documentationErrors).toEqual([])
})

test('landing, quickstart, and API reference form a working round trip', async ({ page }, testInfo) => {
  await page.goto('/')
  await expect(page.getByRole('heading', { level: 1 })).toBeVisible()
  await noOverflow(page)
  await page.getByRole('link', { name: 'Get started', exact: true }).click()
  await expect(page.getByRole('heading', { name: 'Quickstart', exact: true })).toBeVisible()
  await noOverflow(page)
  await page.screenshot({ path: testInfo.outputPath('quickstart.png') })
  await openMainMenu(page)
  await page.locator('.header').getByRole('link', { name: 'API reference', exact: true }).click()
  await expect(page).toHaveURL(/\/docs\/api\/$/)
  await expect(page.getByRole('link', { name: 'User guide', exact: true })).toBeVisible()
  await noOverflow(page)
  await page.getByRole('link', { name: 'User guide', exact: true }).click()
  await expect(page).toHaveURL(/\/docs\/$/)
})

test('historical and current versions share one selector', async ({ page }) => {
  await page.goto('/docs/')
  await page.getByTitle('Show other versions of page').click()
  await page.locator('.version-menu').getByRole('link', { name: 'v0.5.0', exact: true }).click()
  await expect(page).toHaveURL(/\/docs\/v0\.5\.0\/index.html$/)
  await page.getByTitle('Show other versions of page').click()
  await page.locator('.version-menu').getByRole('link', { name: currentVersion, exact: true }).click()
  await expect(page).toHaveURL(/\/docs\/index.html$/)
})

test('search opens a matching integration guide', async ({ page }, testInfo) => {
  await page.goto('/docs/')
  await openMainMenu(page)
  const search = page.getByRole('textbox', { name: 'Search the documentation' })
  await expect(search).toBeVisible()
  await search.pressSequentially('RestClient')
  const result = page.locator('.search-result-dropdown-menu a[href*="clients/spring-restclient.html"]').first()
  await expect(result).toBeVisible()
  const inputBounds = await search.boundingBox()
  const resultBounds = await page.locator('.search-result-dropdown-menu').boundingBox()
  expect(resultBounds.y).toBeGreaterThanOrEqual(inputBounds.y + inputBounds.height)
  expect(resultBounds.x).toBeGreaterThanOrEqual(0)
  expect(resultBounds.x + resultBounds.width).toBeLessThanOrEqual(page.viewportSize().width + 1)
  if (testInfo.project.name === 'mobile') {
    const menuBounds = await page.locator('.navbar-menu').boundingBox()
    expect(resultBounds.y + resultBounds.height).toBeLessThanOrEqual(menuBounds.y + menuBounds.height + 1)
  }
  await noOverflow(page)
  await page.screenshot({ path: testInfo.outputPath('search.png') })
  await result.click()
  await expect(page).toHaveURL(/\/docs\/clients\/spring-restclient.html(?:#.*)?$/)
  await noOverflow(page)
})

test('the documentation sidebar works at both viewport sizes', async ({ page }, testInfo) => {
  await page.goto('/docs/getting-started.html')
  if (testInfo.project.name === 'mobile') await page.locator('.nav-toggle').click()
  await page.locator('.nav-container').getByRole('link', { name: 'Runnable example', exact: true }).click()
  await expect(page).toHaveURL(/\/docs\/runnable-example.html$/)
  await noOverflow(page)
})

async function openMainMenu(page) {
  const toggle = page.getByRole('button', { name: 'Toggle main menu' })
  if (await toggle.isVisible()) await toggle.click()
}

async function noOverflow(page) {
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth + 1)).toBe(true)
}
