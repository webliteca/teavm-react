# Shared HTML Shell

The canonical HTML template for all TeaVM React projects.

## Script loading order

1. **React CDN** (`react.development.js`) -- provides the global `React` object
2. **ReactDOM CDN** (`react-dom.development.js`) -- provides `ReactDOM` for mounting
3. **TeaVM output** (`classes.js`) -- your compiled Java/Kotlin application

This order is critical: `classes.js` references `React` and `ReactDOM` as global objects,
so they must be loaded first.

## Switching to production CDN

In `index.html`, comment out the two `*.development.js` script tags and uncomment the
`*.production.min.js` tags. Production builds are smaller and omit developer warnings.

## Usage

Copy this `index.html` into your project's `src/main/webapp/` directory. The TeaVM Maven
plugin will output `classes.js` alongside it during `mvn process-classes`.
