/*
 * Copyright (c) 2013-2016 Cinchapi Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cinchapi.concourse.server.plugin;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentMap;

import com.cinchapi.concourse.annotate.PackagePrivate;
import com.cinchapi.concourse.server.plugin.io.SharedMemory;
import com.cinchapi.concourse.thrift.AccessToken;
import com.cinchapi.concourse.util.ByteBuffers;
import com.cinchapi.concourse.util.ConcurrentMaps;
import com.cinchapi.concourse.util.Serializables;
import com.google.common.collect.Maps;

/**
 * A {@link Plugin} extends the functionality of Concourse Server.
 * <p>
 * Each class that extends this one may define methods that can be dynamically
 * invoked using the
 * {@link com.cinchapi.concourse.Concourse#invokePlugin(String, String, Object...)
 * invokePlugin} method.
 * </p>
 * 
 * @author Jeff Nelson
 */
public abstract class Plugin {

    /**
     * The name of the dynamic property that is passed to the plugin's JVM to
     * instruct it as to where the plugin's home is located.
     */
    protected final static String PLUGIN_HOME_JVM_PROPERTY = "com.cinchapi.concourse.plugin.home";

    /**
     * A reference to the local Concourse Server {@link ConcourseRuntime
     * runtime} to which this plugin is registered.
     */
    protected final ConcourseRuntime runtime;

    /**
     * The communication channel for messages that come from Concourse Server,
     */
    protected final SharedMemory fromServer;

    /**
     * The communication channel for messages that are sent by this
     * {@link Plugin} to Concourse Server.
     */
    private final SharedMemory fromPlugin;

    /**
     * Upstream response from Concourse Server in response to requests made via
     * {@link ConcourseRuntime}.
     */
    private final ConcurrentMap<AccessToken, RemoteMethodResponse> fromServerResponses;

    /**
     * Construct a new instance.
     * 
     * @param fromServer the location where the main line of communication
     *            between Concourse and the plugin occurs
     * @param notifier an object that the plugin uses to notify of shutdown
     */
    public Plugin(String fromServer, String fromPlugin) {
        this.runtime = ConcourseRuntime.getRuntime();
        this.fromServer = new SharedMemory(fromServer);
        this.fromPlugin = new SharedMemory(fromPlugin);
        this.fromServerResponses = Maps
                .<AccessToken, RemoteMethodResponse> newConcurrentMap();
    }

    /**
     * Start the plugin and process requests until instructed to
     * {@link Instruction#STOP stop}.
     */
    public void run() {
        ByteBuffer data;
        while ((data = fromServer.read()) != null) {
            Instruction type = ByteBuffers.getEnum(data, Instruction.class);
            data = ByteBuffers.getRemaining(data);
            if(type == Instruction.REQUEST) {
                RemoteMethodRequest request = Serializables.read(data,
                        RemoteMethodRequest.class);
                new RemoteInvocationThread(request, fromPlugin, fromServer,
                        this, false, fromServerResponses).start();
            }
            else if(type == Instruction.RESPONSE) {
                RemoteMethodResponse response = Serializables.read(data,
                        RemoteMethodResponse.class);
                ConcurrentMaps.putAndSignal(fromServerResponses,
                        response.creds, response);
            }
            else { // STOP
                break;
            }
        }
    }

    /**
     * Return the {@link PluginConfiguration preferences} for this plugin.
     * <p>
     * The plugin should override this class if the
     * {@link StandardPluginConfiguration} is insufficient.
     * </p>
     * 
     * @return the {@link PluginConfiguration preferences} for the plugin
     */
    protected PluginConfiguration getConfig() {
        return new StandardPluginConfiguration();
    }

    /**
     * High level instructions that are communicated from Concourse Server to
     * the plugin via {@link #fromServer} channel.
     * 
     * @author Jeff Nelson
     */
    @PackagePrivate
    enum Instruction {
        REQUEST, RESPONSE, STOP, MESSAGE
    }

}