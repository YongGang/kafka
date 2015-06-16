/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kafka.common.network;

/*
 * Transport layer for PLAINTEXT communication
 */

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;

import java.security.Principal;

import org.apache.kafka.common.security.auth.KafkaPrincipal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainTextTransportLayer implements TransportLayer {
    private static final Logger log = LoggerFactory.getLogger(PlainTextTransportLayer.class);
    private SelectionKey key;
    private SocketChannel socketChannel;
    private final Principal principal = new KafkaPrincipal("ANONYMOUS");

    public PlainTextTransportLayer(SelectionKey key) throws IOException {
        this.key = key;
        this.socketChannel = (SocketChannel) key.channel();
    }

    public boolean isReady() {
        return true;
    }

    public void finishConnect() throws IOException {
        socketChannel.finishConnect();
        int ops = key.interestOps();
        ops &= ~SelectionKey.OP_CONNECT;
        ops |= SelectionKey.OP_READ;
        key.interestOps(ops);
    }

    public void disconnect() {
        key.cancel();
    }

    public SocketChannel socketChannel() {
        return socketChannel;
    }


    public boolean isOpen() {
        return socketChannel.isOpen();
    }

    /**
     * Closes this channel
     *
     * @throws IOException If and I/O error occurs
     */
    public void close() throws IOException {
        socketChannel.socket().close();
        socketChannel.close();
    }



    /**
     * There won't be any pending bytes to written socketChannel once write method is called.
     * This will always return false.
     */
    public boolean pending() {
        return false;
    }


    /**
     * Performs SSL handshake hence is a no-op for the non-secure
     * implementation
     * @throws IOException
    */
    public void handshake() throws IOException {}


    /**
    * Reads a sequence of bytes from this channel into the given buffer.
    *
    * @param dst The buffer into which bytes are to be transferred
    * @return The number of bytes read, possible zero or -1 if the channel has reached end-of-stream
    * @throws IOException if some other I/O error occurs
    */

    public int read(ByteBuffer dst) throws IOException {
        return socketChannel.read(dst);
    }

    /**
     * Reads a sequence of bytes from this channel into the given buffers.
     *
     * @param dsts - The buffers into which bytes are to be transferred.
     * @return The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream.
     * @throws IOException if some other I/O error occurs
     */
    public long read(ByteBuffer[] dsts) throws IOException {
        return socketChannel.read(dsts);
    }

    /**
     * Reads a sequence of bytes from this channel into a subsequence of the given buffers.
     * @param dsts - The buffers into which bytes are to be transferred
     * @param offset - The offset within the buffer array of the first buffer into which bytes are to be transferred; must be non-negative and no larger than dsts.length.
     * @param length - The maximum number of buffers to be accessed; must be non-negative and no larger than dsts.length - offset
     * @returns The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream.
     * @throws IOException if some other I/O error occurs
     */
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        return socketChannel.read(dsts, offset, length);
    }

    /**
    * Writes a sequence of bytes to this channel from the given buffer.
    *
    * @param src The buffer from which bytes are to be retrieved
    * @returns The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream
    * @throws IOException If some other I/O error occurs
    */
    public int write(ByteBuffer src) throws IOException {
        return socketChannel.write(src);
    }

    /**
    * Writes a sequence of bytes to this channel from the given buffer.
    *
    * @param src The buffer from which bytes are to be retrieved
    * @returns The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream
    * @throws IOException If some other I/O error occurs
    */
    public long write(ByteBuffer[] srcs) throws IOException {
        return socketChannel.write(srcs);
    }

    /**
    * Writes a sequence of bytes to this channel from the subsequence of the given buffers.
    *
    * @param srcs The buffers from which bytes are to be retrieved
    * @param offset The offset within the buffer array of the first buffer from which bytes are to be retrieved; must be non-negative and no larger than srcs.length.
    * @param length - The maximum number of buffers to be accessed; must be non-negative and no larger than srcs.length - offset.
    * @return returns no.of bytes written , possibly zero.
    * @throws IOException If some other I/O error occurs
    */
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        return socketChannel.write(srcs, offset, length);
    }

    /**
     * Rerturns ANONYMOUS as Principal.
     */
    public Principal peerPrincipal() throws IOException {
        return principal;
    }

    /**
     * Adds the interestOps to selectionKey.
     * @param SelectionKey interestOps
     */
    public void addInterestOps(int ops) {
        key.interestOps(key.interestOps() | ops);

    }

    /**
     * Removes the interestOps from selectionKey.
     * @param SelectionKey interestOps
     */
    public void removeInterestOps(int ops) {
        key.interestOps(key.interestOps() & ~ops);
    }

}
