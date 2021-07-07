config.set({
    "browsers": ["MyChromeHeadless"],
    "browserNoActivityTimeout": 10000000000,
    "pingTimeout": 10000000000,
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
        captureConsole: true,
        "mocha": {
            timeout: 10000000000
        }
    }
});
