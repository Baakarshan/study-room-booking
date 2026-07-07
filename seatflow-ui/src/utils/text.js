const windows1252 = {
  '\u20ac': 0x80,
  '\u201a': 0x82,
  '\u0192': 0x83,
  '\u201e': 0x84,
  '\u2026': 0x85,
  '\u2020': 0x86,
  '\u2021': 0x87,
  '\u02c6': 0x88,
  '\u2030': 0x89,
  '\u0160': 0x8a,
  '\u2039': 0x8b,
  '\u0152': 0x8c,
  '\u017d': 0x8e,
  '\u2018': 0x91,
  '\u2019': 0x92,
  '\u201c': 0x93,
  '\u201d': 0x94,
  '\u2022': 0x95,
  '\u2013': 0x96,
  '\u2014': 0x97,
  '\u02dc': 0x98,
  '\u2122': 0x99,
  '\u0161': 0x9a,
  '\u203a': 0x9b,
  '\u0153': 0x9c,
  '\u017e': 0x9e,
  '\u0178': 0x9f
}

const mojibakePattern = /[\u00c0-\u00ff\u0160-\u0178\u02c6\u02dc\u2018-\u201e\u20ac]/

export function fixMojibake(value) {
  if (typeof value !== 'string' || !mojibakePattern.test(value)) {
    return value
  }

  const bytes = []
  for (const char of value) {
    const code = char.charCodeAt(0)
    if (windows1252[char] !== undefined) {
      bytes.push(windows1252[char])
    } else if (code <= 255) {
      bytes.push(code)
    } else {
      return value
    }
  }

  try {
    const decoded = new TextDecoder('utf-8', { fatal: true }).decode(new Uint8Array(bytes))
    return /[\u4e00-\u9fff]/.test(decoded) ? decoded : value
  } catch (error) {
    return value
  }
}
