import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { interviewApi } from '../lib/api';
import type { Interview } from '../types';
import { toast } from 'sonner';

export default function DashboardPage() {
  const { user, signOut } = useAuth();
  const navigate = useNavigate();
  const [interviews, setInterviews] = useState<Interview[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user?.id) {
      loadInterviews();
    }
  }, [user]);

  const loadInterviews = async () => {
    try {
      const response = await interviewApi.getByUserId(user!.id);
      setInterviews(response.data || []);
    } catch {
      // silent
    } finally {
      setLoading(false);
    }
  };

  const handleSignOut = () => {
    signOut();
    toast.success('Signed out successfully');
    navigate('/sign-in');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex items-center justify-between">
          <h1 className="text-2xl font-bold text-gray-900">IntervueAI</h1>
          <div className="flex items-center gap-4">
            <span className="text-sm text-gray-600">{user?.name}</span>
            <button
              onClick={handleSignOut}
              className="px-4 py-2 text-sm bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition"
            >
              Sign Out
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* New Interview Button */}
        <div className="mb-8">
          <button
            onClick={() => navigate('/interview/new')}
            className="px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition shadow-sm"
          >
            + New Interview
          </button>
        </div>

        {/* Interviews Grid */}
        {loading ? (
          <div className="flex justify-center py-20">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
        ) : interviews.length === 0 ? (
          <div className="text-center py-20">
            <h3 className="text-lg font-medium text-gray-900">No interviews yet</h3>
            <p className="text-gray-500 mt-2">Start your first mock interview to get AI-powered feedback!</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {interviews.map((interview) => (
              <div
                key={interview.id}
                onClick={() => navigate(`/interview/${interview.id}`)}
                className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:shadow-md transition cursor-pointer"
              >
                <div className="flex items-center justify-between mb-3">
                  <span className="px-2.5 py-0.5 bg-blue-100 text-blue-700 text-xs font-medium rounded-full">
                    {interview.type}
                  </span>
                  <span className="px-2.5 py-0.5 bg-gray-100 text-gray-600 text-xs font-medium rounded-full">
                    {interview.level}
                  </span>
                </div>
                <h3 className="text-lg font-semibold text-gray-900 mb-2">{interview.role}</h3>
                <div className="flex flex-wrap gap-1.5 mb-3">
                  {interview.techstack?.slice(0, 4).map((tech, i) => (
                    <span key={i} className="px-2 py-0.5 bg-gray-50 text-gray-600 text-xs rounded border border-gray-200">
                      {tech}
                    </span>
                  ))}
                </div>
                <p className="text-xs text-gray-400">
                  {interview.createdAt ? new Date(interview.createdAt).toLocaleDateString() : ''}
                </p>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}

