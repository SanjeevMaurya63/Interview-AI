import axios from 'axios';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/sign-in';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authApi = {
  signUp: (name: string, email: string, password: string) =>
    api.post('/auth/signup', { name, email, password }),

  signIn: (email: string, password: string) =>
    api.post('/auth/signin', { email, password }),

  getMe: () => api.get('/auth/me'),
};

// Interview API
export const interviewApi = {
  getByUserId: (userId: string) =>
    api.get(`/interviews/user/${userId}`),

  getLatest: (userId: string) =>
    api.get(`/interviews/latest?userId=${userId}`),

  getById: (id: string) =>
    api.get(`/interviews/${id}`),

  generate: (data: {
    type: string;
    role: string;
    level: string;
    techstack: string;
    amount: number;
    profile: string;
    userid: string;
  }) => api.post('/interviews/generate', data),
};

// Feedback API
export const feedbackApi = {
  create: (data: {
    interviewId: string;
    userId: string;
    transcript: { role: string; content: string }[];
    feedbackId?: string;
  }) => api.post('/feedback/create', data),

  getByInterviewId: (interviewId: string, userId: string) =>
    api.get(`/feedback/interview/${interviewId}?userId=${userId}`),
};

export default api;

