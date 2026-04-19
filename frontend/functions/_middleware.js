export async function onRequest(context) {
  const { request, env } = context;
  const url = new URL(request.url);

  // 检查是否是 API 请求
  if (url.pathname.startsWith('/api')) {
    const backendUrl = 'https://2bbb2ca3.r20.vip.cpolar.cn';

    // 构建目标 URL（去掉 /api 前缀）
    const targetUrl = backendUrl + url.pathname.replace('/api', '') + url.search;

    // 创建新请求
    const modifiedRequest = new Request(targetUrl, {
      method: request.method,
      headers: request.headers,
      body: request.body,
      redirect: 'manual'
    });

    // 设置正确的 Host 头
    modifiedRequest.headers.set('Host', new URL(backendUrl).host);
    modifiedRequest.headers.set('X-Forwarded-Host', url.hostname);

    // 处理 CORS 预检请求
    if (request.method === 'OPTIONS') {
      return new Response(null, {
        status: 204,
        headers: {
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
          'Access-Control-Allow-Headers': 'Content-Type, Authorization',
          'Access-Control-Allow-Credentials': 'true'
        }
      });
    }

    // 发送请求到后端
    try {
      const response = await fetch(modifiedRequest);

      // 复制响应并添加 CORS 头
      const modifiedResponse = new Response(response.body, {
        status: response.status,
        statusText: response.statusText,
        headers: response.headers
      });

      // 添加 CORS 头
      modifiedResponse.headers.set('Access-Control-Allow-Origin', '*');
      modifiedResponse.headers.set('Access-Control-Allow-Credentials', 'true');

      return modifiedResponse;
    } catch (error) {
      return new Response(JSON.stringify({ error: 'Proxy error', message: error.message }), {
        status: 500,
        headers: { 'Content-Type': 'application/json' }
      });
    }
  }

  // 非 API 请求，返回静态资源
  return env.ASSETS.fetch(request);
}
