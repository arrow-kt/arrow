config.set({
  "browsers": ["MyChromeHeadless"],
  "customLaunchers": {
      "MyChromeHeadless": {
          base: "ChromeHeadless",
          flags: [
              "--allow-failed-policy-fetch-for-test",
              "--allow-external-pages",
              "--no-sandbox",
              "--disable-web-security",
              "--disable-setuid-sandbox",
              "--enable-logging",
              "--v=1"
          ]
      }
  },
  "client": {
    "mocha": {
      "timeout": 600000
    },
  },
//  "pingTimeout": 600000,
//  "browserNoActivityTimeout": 600000,
//  "browserDisconnectTimeout": 600000
});