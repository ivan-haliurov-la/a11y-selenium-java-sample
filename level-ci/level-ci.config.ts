import type { Config } from "@level-ci/cli";
export default {
  organization: "ivan-haliurov-7-userway-org",
  project: "a11y-selenium-java-sample",
  token: process.env.LEVEL_CI_TOKEN,
  reportPaths: ["./level-ci-reports"],
  server: "https://api.dev.userway.dev",
} satisfies Config;
