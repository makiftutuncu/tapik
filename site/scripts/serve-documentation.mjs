import { createServer } from 'node:http'
import { readFile, realpath, stat } from 'node:fs/promises'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../target/site')
const contentTypes = {
  '.html': 'text/html; charset=utf-8', '.css': 'text/css', '.js': 'text/javascript', '.json': 'application/json',
  '.svg': 'image/svg+xml', '.png': 'image/png', '.ico': 'image/x-icon', '.woff': 'font/woff', '.woff2': 'font/woff2'
}

createServer(async (request, response) => {
  if (!['GET', 'HEAD'].includes(request.method)) { response.writeHead(405).end(); return }
  try {
    const url = new URL(request.url, 'http://127.0.0.1:4173')
    let file = await realpath(path.join(root, decodeURIComponent(url.pathname)))
    if (!file.startsWith(root + path.sep) && file !== root) { response.writeHead(403).end(); return }
    if ((await stat(file)).isDirectory()) {
      if (!url.pathname.endsWith('/')) { response.writeHead(302, { Location: url.pathname + '/' }).end(); return }
      file = await realpath(path.join(file, 'index.html'))
    }
    if (!file.startsWith(root + path.sep)) { response.writeHead(403).end(); return }
    const contents = await readFile(file)
    response.writeHead(200, { 'Content-Type': contentTypes[path.extname(file)] || 'application/octet-stream' })
    response.end(request.method === 'HEAD' ? undefined : contents)
  } catch {
    response.writeHead(404).end('Not found')
  }
}).listen(4173, '127.0.0.1', () => console.log('Serving tapik documentation at http://127.0.0.1:4173'))
