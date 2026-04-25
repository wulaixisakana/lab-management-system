export async function onRequest(context) {
  const { request, env } = context;
  const url = new URL(request.url);

  // 后端 API 地址
  const backendUrl = (env && env.BACKEND_URL) ? env.BACKEND_URL : 'https://2bbb2ca3.r20.vip.cpolar.cn';

  // 构建目标 URL
  const targetUrl = backendUrl + url.pathname.replace('/api', '') + url.search;

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

  // 只保留必要的请求头
  const cleanHeaders = {
    'Content-Type': request.headers.get('Content-Type') || 'application/json',
  };
  const auth = request.headers.get('Authorization');
  if (auth) cleanHeaders['Authorization'] = auth;

  try {
    const response = await fetch(targetUrl, {
      method: request.method,
      headers: cleanHeaders,
      body: request.method !== 'GET' && request.method !== 'HEAD' ? request.body : undefined,
    });

    const modifiedResponse = new Response(response.body, {
      status: response.status,
      statusText: response.statusText,
      headers: response.headers
    });
    const requestOrigin = request.headers.get('Origin') || '*';
    modifiedResponse.headers.set('Access-Control-Allow-Origin', requestOrigin);
    modifiedResponse.headers.set('Access-Control-Allow-Credentials', 'true');

    return modifiedResponse;
  } catch (error) {
    return new Response(JSON.stringify({ error: 'Proxy error', message: error.message }), {
      status: 500,
      headers: { 'Content-Type': 'application/json' }
    });
  }
}
