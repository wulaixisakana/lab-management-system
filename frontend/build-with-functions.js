const fs = require('fs');
const path = require('path');

console.log('Copying functions to dist...');
const functionsDir = path.join(__dirname, 'functions');
const distFunctionsDir = path.join(__dirname, 'dist', 'functions');

function copyRecursiveSync(src, dest) {
  const stat = fs.statSync(src);
  if (stat.isDirectory()) {
    if (!fs.existsSync(dest)) {
      fs.mkdirSync(dest, { recursive: true });
    }
    const entries = fs.readdirSync(src);
    entries.forEach(entry => {
      copyRecursiveSync(path.join(src, entry), path.join(dest, entry));
    });
    return;
  }

  fs.copyFileSync(src, dest);
}

if (!fs.existsSync(distFunctionsDir)) {
  fs.mkdirSync(distFunctionsDir, { recursive: true });
}

if (fs.existsSync(functionsDir)) {
  copyRecursiveSync(functionsDir, distFunctionsDir);
}

console.log('Build completed with functions!');
