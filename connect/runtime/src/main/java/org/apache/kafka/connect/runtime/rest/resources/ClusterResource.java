/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime.rest.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.kafka.connect.runtime.Herder;
import org.apache.kafka.connect.runtime.rest.HerderRequestHandler;
import org.apache.kafka.connect.runtime.rest.RestClient;
import org.apache.kafka.connect.util.FutureCallback;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

@Path("/cluster")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClusterResource implements ConnectResource {

    private final Herder herder;
    private final HerderRequestHandler requestHandler;

    public ClusterResource(Herder herder, RestClient restClient) {
        this.herder = herder;
        this.requestHandler = new HerderRequestHandler(restClient, DEFAULT_REST_REQUEST_TIMEOUT_MS);
    }

    @Override
    public void requestTimeout(long requestTimeoutMs) {
        requestHandler.requestTimeoutMs(requestTimeoutMs);
    }

    @PUT
    @Path("/{clusterId}/rebalance")
    @Operation(summary = "Trigger a rebalance on a Connect Cluster. If premept-scheduled-rebalance is set to true, then " +
        "the rebalance would not wait for any departed workers to come back if scheduled rebalance is active and instead " +
        "reassign their assignments immediately.")
    public void removeWorkerFromGroup(final @PathParam("clusterId") String clusterId,
                                      final @Context HttpHeaders headers,
                                      final @DefaultValue("false") @QueryParam("preemptScheduledRebalance") @Parameter(description = "Preempt any active Scheduled rebalance delay") Boolean preemptScheduledRebalance,
                                      final @Parameter(hidden = true) @QueryParam("forward") Boolean forward) throws Throwable {
        FutureCallback<Void> cb = new FutureCallback<>();
        herder.triggerRebalance(clusterId, preemptScheduledRebalance, cb);
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("preemptScheduledRebalance", preemptScheduledRebalance.toString());
        String forwardingPath = "/cluster/" + clusterId + "/rebalance";
        requestHandler.completeOrForwardRequest(cb, forwardingPath, "PUT", headers, queryParameters, null, null, new HerderRequestHandler.IdentityTranslator<>(), forward);
    }

}
