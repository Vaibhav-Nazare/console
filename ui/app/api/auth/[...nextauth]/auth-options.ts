import { getKafkaClusters } from "@/api/kafka/actions";
import { ClusterList } from "@/api/kafka/schema";
import { logger } from "@/utils/logger";
import { AuthOptions } from "next-auth";
import { Provider } from "next-auth/providers/index";
import { makeAnonymous } from "./anonymous";
import { makeOauthTokenProvider } from "./oauth-token";
import { makeScramShaProvider } from "./scram";

const log = logger.child({ module: "auth" });

function makeAuthOption(cluster: ClusterList): Provider {
  switch (cluster.meta.authentication?.method) {
    case "oauth": {
      const { tokenUrl } = cluster.meta.authentication;
      return makeOauthTokenProvider(tokenUrl ?? "TODO");
    }
    case "basic":
      return makeScramShaProvider(cluster.id);
    case "anonymous":
    default:
      return makeAnonymous();
  }
}

export async function getAuthOptions(): Promise<AuthOptions> {
  // retrieve the authentication method required by the default Kafka cluster
  const clusters = await getKafkaClusters();
  const providers = clusters.map(makeAuthOption);
  log.trace({ providers }, "getAuthOptions");
  return {
    providers,
    callbacks: {
      async jwt({ token, user }) {
        if (user) {
          token.authorization = user.authorization;
        }
        return token;
      },
      async session({ session, token, user }) {
        // Send properties to the client, like an access_token and user id from a provider.
        session.authorization = token.authorization;

        return session;
      },
    },
  };
}