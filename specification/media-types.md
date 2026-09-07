# HTTP media types

`MediaType` represents a concrete body media type, not an `Accept` range. It uses the token, quoted-string, and
parameter grammar of [RFC 9110 sections 5.6 and 8.3](https://www.rfc-editor.org/rfc/rfc9110.html#section-8.3.1),
without a Spring dependency. Construction rejects invalid syntax with `IllegalArgumentException` and a position
and reason, before a body or target can use it.

## Syntax and rendering

- Require non-empty ASCII HTTP tokens for type, subtype, and parameter names, and exactly one separating slash.
  Wildcards in type/subtype are rejected: media ranges belong to negotiation, not body definitions.
- Accept token or double-quoted parameter values, including valid quoted-pair escapes and empty quoted values.
  Quoted strings allow HTTP whitespace and Latin-1 octets, but reject CR, LF, other controls, DEL, and code points
  beyond Latin-1. Charset values must themselves decode to a non-empty token.
- Allow spaces and tabs around semicolon separators and around the complete input, but not around `/` or `=`.
  Empty semicolon groups are accepted as permitted by the HTTP grammar and omitted from normalized output.
- Reject duplicate parameter names case-insensitively, even when values agree, rather than choosing a winner.
- `value` and `toString()` return normalized wire syntax: lowercase type/subtype and parameter names, no separator
  whitespace, unquoted token values, and escaped quoted strings when quoting is required. Retain parameter order.

## Identity

Type/subtype and parameter names compare case-insensitively. Parameter order and equivalent quoting/escaping do
not affect equality or hashing. Charset values compare case-insensitively; other values are opaque and case-sensitive,
including multipart boundaries and profile identifiers. No charset aliases, implicit charset defaults, registry
lookups, or additional media-specific parameter semantics are inferred.

Thus `Text/Plain;Charset="UTF-8"` equals `text/plain;charset=utf-8`, but `multipart/mixed;boundary=A` does not equal
`multipart/mixed;boundary=a`. Different explicit parameters remain distinct representations.

## Targets

Body uniqueness uses `MediaType` equality. OpenAPI Content Object keys and generated Spring mappings use its
normalized, validated `value`. Malformed contract media types never reach document generation or Spring startup.
This validates neutral syntax, not whether a target has a codec or supports a particular charset or media type.
`Accept` quality, wildcard selection, and compatibility are separate concerns and are unchanged by this task.
