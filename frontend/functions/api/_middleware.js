export async function onRequest(context) {
  const { request, env } = context;
  const url = new URL(request.url);

  // 后端 API 地址（cpolar 或本地）
  const backendUrl = 'https://2bbb2ca3.r20.vip.cpolar.cn';

  // 构建目标 URL
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

  // 处理 CORS
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
  const response = await fetch(modifiedRequest);

  // 复制响应并添加 CORS 头
  const modifiedResponse = new Response(response.body, response);
  modifiedResponse.headers.set('Access-Control-Allow-Origin', '*');
  modifiedResponse.headers.set('Access-Control-Allow-Credentials', 'true');

  return modifiedResponse;
}
