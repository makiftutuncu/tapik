import assert from 'node:assert/strict'
import { readFile, readdir } from 'node:fs/promises'
import path from 'node:path'
import test from 'node:test'

const pagesDirectory = path.resolve('docs', 'modules')
const sourceBlock = /^\[source,(kotlin|xml)\]\r?\n----\r?\n([\s\S]*?)\r?\n----$/gm

test('collects substantial executable documentation examples from tested source', async () => {
  const copiedBlocks = []
  for (const file of await findAsciiDocFiles(pagesDirectory)) {
    const contents = await readFile(file, 'utf8')
    for (const match of contents.matchAll(sourceBlock)) {
      const body = match[2]
      const sourceLines = body.split(/\r?\n/).filter(line => line.trim() !== '')
      if (sourceLines.length > 1 && !body.includes('include::example$')) {
        copiedBlocks.push(`${path.relative(process.cwd(), file)}:${lineNumber(contents, match.index)}`)
      }
    }
  }

  assert.deepEqual(
    copiedBlocks,
    [],
    `Multiline Kotlin and XML examples must use Antora Collector imports:\n${copiedBlocks.join('\n')}`
  )
})

async function findAsciiDocFiles(directory) {
  const files = []
  for (const entry of await readdir(directory, { withFileTypes: true })) {
    const child = path.join(directory, entry.name)
    if (entry.isDirectory()) files.push(...(await findAsciiDocFiles(child)))
    else if (entry.isFile() && entry.name.endsWith('.adoc')) files.push(child)
  }
  return files.sort()
}

function lineNumber(contents, index) {
  return contents.slice(0, index).split(/\r?\n/).length
}
