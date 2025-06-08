package com.zjfgh.bluedhook.simple;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class WSServerManager {
    private static final String TAG = "WSServerManager";

    private WebSocketServer mWebSocketServer;
    private int mPort;
    private final WSServerListener mListener;
    private final List<WebSocket> mConnections = new ArrayList<>();
    private volatile boolean mIsRunning = false;

    public interface WSServerListener {
        void onServerStarted(int port);

        void onServerStopped();

        void onServerError(String error);

        void onClientConnected(String address);

        void onClientDisconnected(String address);

        void onMessageReceived(WebSocket conn, String message);
    }

    public WSServerManager(WSServerListener listener) {
        this.mListener = listener;
    }

    /**
     * 启动WebSocket服务器
     */
    public void startServer(int port) {
        this.mPort = port;
        if (mIsRunning) {
            Log.w(TAG, "Server is already running");
            if (mListener != null) {
                mListener.onServerError("Server is already running");
            }
            return;
        }

        try {
            mWebSocketServer = new WebSocketServer(new InetSocketAddress(mPort)) {
                @Override
                public void onOpen(WebSocket conn, ClientHandshake handshake) {
                    String clientAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress();
                    Log.d(TAG, "New connection from: " + clientAddress);
                    mConnections.add(conn);

                    if (mListener != null) {
                        mListener.onClientConnected(clientAddress);
                    }
                }

                @Override
                public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                    String clientAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress();
                    Log.d(TAG, "Connection closed from: " + clientAddress + ", reason: " + reason);
                    mConnections.remove(conn);

                    if (mListener != null) {
                        mListener.onClientDisconnected(clientAddress);
                    }
                }

                @Override
                public void onMessage(WebSocket conn, String message) {
                    String clientAddress = conn.getRemoteSocketAddress().getAddress().getHostAddress();
                    Log.d(TAG, "Message from " + clientAddress + ": " + message);

                    if (mListener != null) {
                        mListener.onMessageReceived(conn, message);
                    }
                }

                @Override
                public void onError(WebSocket conn, Exception ex) {
                    String error = ex != null ? ex.getMessage() : "Unknown error";
                    Log.e(TAG, "WebSocket error: " + error);

                    if (mListener != null) {
                        mListener.onServerError(error);
                    }
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "WebSocket server started on port " + mPort);
                    mIsRunning = true;

                    if (mListener != null) {
                        mListener.onServerStarted(mPort);
                    }
                }
            };

            mWebSocketServer.start();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error starting server: " + e.getMessage());
            if (mListener != null) {
                mListener.onServerError(e.getMessage());
            }
        }
    }

    /**
     * 停止WebSocket服务器
     */
    public void stopServer() {
        if (!mIsRunning) {
            Log.w(TAG, "Server is not running");
            if (mListener != null) {
                mListener.onServerError("Server is not running");
            }
            return;
        }

        try {
            // 关闭所有连接
            for (WebSocket conn : mConnections) {
                try {
                    conn.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing connection: " + e.getMessage());
                }
            }
            mConnections.clear();

            // 停止服务器
            mWebSocketServer.stop();
            mWebSocketServer = null;
            mIsRunning = false;

            Log.d(TAG, "WebSocket server stopped");
            if (mListener != null) {
                mListener.onServerStopped();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping server: " + e.getMessage());
            if (mListener != null) {
                mListener.onServerError(e.getMessage());
            }
        }
    }

    /**
     * 向所有连接的客户端广播消息
     */
    public void broadcastMessage(String message) {
        if (!mIsRunning || mWebSocketServer == null) {
            Log.w(TAG, "Cannot broadcast - server is not running");
            return;
        }

        try {
            mWebSocketServer.broadcast(message);
        } catch (Exception e) {
            Log.e(TAG, "Error broadcasting message: " + e.getMessage());
        }
    }

    /**
     * 向特定客户端发送消息
     */
    public void sendMessage(WebSocket conn, String message) {
        if (conn != null && mIsRunning) {
            try {
                conn.send(message);
            } catch (Exception e) {
                Log.e(TAG, "Error sending message: " + e.getMessage());
            }
        }
    }

    /**
     * 获取当前连接的客户端数量
     */
    public int getConnectedClientsCount() {
        return mConnections.size();
    }

    /**
     * 检查服务器是否正在运行
     */
    public boolean isServerRunning() {
        return mIsRunning;
    }

    /**
     * 获取服务器端口
     */
    public int getPort() {
        return mPort;
    }

    /**
     * 获取所有连接的客户端
     */
    public List<WebSocket> getConnections() {
        return new ArrayList<>(mConnections);
    }
}