const { defineConfig } = require("cypress");

module.exports = defineConfig({
  e2e: {
    baseUrl: "http://localhost:8081",
    specPattern: "cypress/e2e/**/*.cy.{js,jsx,ts,tsx}",
    supportFile: false,
    setupNodeEvents(on, config) {
      if (typeof config.baseUrl === "string") {
        const normalized = config.baseUrl.replace(/\/$/, "");
        if (/^https?:\/\/localhost$/i.test(normalized)) {
          config.baseUrl = "http://localhost:8081";
        }
      }

      return config;
    },
  },
});
