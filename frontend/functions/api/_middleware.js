export async function onRequest(context) {
  const { request, env } = context;
  const url = new URL(request.url);

  // 后端 API 地址（cpolar 或本地）
  const backendUrl = (env && env.BACKEND_URL) ? env.BACKEND_URL : 'https://2bbb2ca3.r20.vip.cpolar.cn';

  // 构建目标 URL
  const targetUrl = backendUrl + url.pathname.replace('/api', '') + url.search;

  // 创建新请求
  const headers = new Headers(request.headers);
  // 设置正确的 Host 头（必须在创建 Request 之前）
  headers.set('Host', new URL(backendUrl).host);
  headers.set('X-Forwarded-Host', url.hostname);
  headers.delete('cf-connecting-ip');
  headers.delete('cf-ray');

  const modifiedRequest = new Request(targetUrl, {
    method: request.method,
    headers,
    body: request.body,
    redirect: 'manual'
  });

  // 处理 CORS
  if (request.method === 'OPTIONS') {
    const requestOrigin = request.headers.get('Origin') || '*';
    return new Response(null, {
      status: 204,
      headers: {
        'Access-Control-Allow-Origin': requestOrigin,
        'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type, Authorization',
        'Access-Control-Allow-Credentials': 'true'
      }
    });
  }

  // 发送请求到后端
  const response = await fetch(modifiedRequest);

  // 复制响应并添加 CORS 头
  const modifiedResponse = new Response(response.body, response);
  const requestOrigin = request.headers.get('Origin') || '*';
  modifiedResponse.headers.set('Access-Control-Allow-Origin', requestOrigin);
  modifiedResponse.headers.set('Access-Control-Allow-Credentials', 'true');

  return modifiedResponse;
}
