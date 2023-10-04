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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/workers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkersResource implements ConnectResource {

    private static final Logger log = LoggerFactory.getLogger(ConnectorsResource.class);

    private final Herder herder;
    private final HerderRequestHandler requestHandler;

    public WorkersResource(Herder herder, RestClient restClient) {
        this.herder = herder;
        this.requestHandler = new HerderRequestHandler(restClient, DEFAULT_REST_REQUEST_TIMEOUT_MS);
    }

    @Override
    public void requestTimeout(long requestTimeoutMs) {
        requestHandler.requestTimeoutMs(requestTimeoutMs);
    }

    @DELETE
    @Path("/{groupInstanceId}")
    @Operation(summary = "Remove a worker from a Connect cluster having static membership enabled.")
    public void removeWorkerFromGroup(final @PathParam("groupInstanceId") String groupInstanceId,
                                      final @Context HttpHeaders headers,
                                      final @Parameter(hidden = true) @QueryParam("forward") Boolean forward) throws Throwable {
        FutureCallback<Void> cb = new FutureCallback<>();
        herder.removeWorkerFromGroup(groupInstanceId, cb);
        requestHandler.completeOrForwardRequest(cb, "/workers/" + groupInstanceId, "DELETE", headers, null, forward);
    }
}
