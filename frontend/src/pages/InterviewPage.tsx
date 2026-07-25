import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { interviewApi, feedbackApi } from '../lib/api';
import type { Interview, Feedback } from '../types';

export default function InterviewPage() {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [interview, setInterview] = useState<Interview | null>(null);
  const [feedback, setFeedback] = useState<Feedback | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id && id !== 'new' && user) {
      loadData();
    } else if (id === 'new') {
      setLoading(false);
    }
  }, [id, user]);

  const loadData = async () => {
    try {
      const [interviewRes, feedbackRes] = await Promise.all([
        interviewApi.getById(id!),
        feedbackApi.getByInterviewId(id!, user!.id).catch(() => null),
      ]);
      setInterview(interviewRes.data);
      if (feedbackRes?.data) {
        setFeedback(feedbackRes.data);
      }
    } catch {
      // silent
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (id === 'new') {
    return (
      <div className="min-h-screen bg-gray-50">
        <header className="bg-white shadow-sm border-b">
          <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
            <h1 className="text-xl font-bold text-gray-900">New Interview</h1>
            <button onClick={() => navigate('/dashboard')} className="text-sm text-gray-600 hover:text-gray-900">
              Back to Dashboard
            </button>
          </div>
        </header>
        <main className="max-w-2xl mx-auto px-4 py-8">
          <div className="bg-white rounded-xl shadow-sm border p-8 text-center">
            <h2 className="text-lg font-semibold text-gray-900 mb-2">Interview Generation</h2>
            <p className="text-gray-500 mb-6">
              To generate a new mock interview, please use the Vapi voice assistant integration or configure the interview settings through the backend API.
            </p>
            <button
              onClick={() => navigate('/dashboard')}
              className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition"
            >
              Go Back
            </button>
          </div>
        </main>
      </div>
    );
  }

  if (!interview) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold text-gray-900">Interview not found</h2>
          <button onClick={() => navigate('/dashboard')} className="mt-4 text-blue-600 hover:text-blue-700">
            Back to Dashboard
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-between">
          <h1 className="text-xl font-bold text-gray-900">{interview.role}</h1>
          <button onClick={() => navigate('/dashboard')} className="text-sm text-gray-600 hover:text-gray-900">
            Back to Dashboard
          </button>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-4 py-8 space-y-6">
        {/* Interview Info */}
        <div className="bg-white rounded-xl shadow-sm border p-6">
          <div className="flex items-center gap-3 mb-4">
            <span className="px-3 py-1 bg-blue-100 text-blue-700 text-sm font-medium rounded-full">
              {interview.type}
            </span>
            <span className="px-3 py-1 bg-gray-100 text-gray-600 text-sm font-medium rounded-full">
              {interview.level}
            </span>
          </div>
          <div className="flex flex-wrap gap-2 mb-4">
            {interview.techstack?.map((tech, i) => (
              <span key={i} className="px-2.5 py-1 bg-gray-50 text-gray-700 text-sm rounded border border-gray-200">
                {tech}
              </span>
            ))}
          </div>
        </div>

        {/* Questions */}
        <div className="bg-white rounded-xl shadow-sm border p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Interview Questions</h2>
          <ol className="space-y-3 list-decimal list-inside">
            {interview.questions?.map((q, i) => (
              <li key={i} className="text-gray-700 leading-relaxed">{q}</li>
            ))}
          </ol>
        </div>

        {/* Feedback */}
        {feedback && (
          <div className="bg-white rounded-xl shadow-sm border p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">AI Feedback</h2>

            <div className="mb-6">
              <div className="flex items-center gap-3 mb-2">
                <span className="text-sm font-medium text-gray-700">Total Score:</span>
                <span className={`text-2xl font-bold ${feedback.totalScore >= 70 ? 'text-green-600' :
                    feedback.totalScore >= 40 ? 'text-yellow-600' : 'text-red-600'
                  }`}>
                  {feedback.totalScore}/100
                </span>
              </div>
            </div>

            {/* Category Scores */}
            <div className="space-y-3 mb-6">
              {feedback.categoryScores?.map((cat, i) => (
                <div key={i} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-medium text-gray-900">{cat.name}</p>
                    <p className="text-sm text-gray-500">{cat.comment}</p>
                  </div>
                  <span className={`text-lg font-bold ${cat.score >= 70 ? 'text-green-600' :
                      cat.score >= 40 ? 'text-yellow-600' : 'text-red-600'
                    }`}>
                    {cat.score}
                  </span>
                </div>
              ))}
            </div>

            {/* Strengths */}
            <div className="mb-4">
              <h3 className="font-medium text-green-700 mb-2">✓ Strengths</h3>
              <ul className="space-y-1">
                {feedback.strengths?.map((s, i) => (
                  <li key={i} className="text-sm text-gray-700">• {s}</li>
                ))}
              </ul>
            </div>

            {/* Areas for Improvement */}
            <div className="mb-4">
              <h3 className="font-medium text-red-700 mb-2">△ Areas for Improvement</h3>
              <ul className="space-y-1">
                {feedback.areasForImprovement?.map((a, i) => (
                  <li key={i} className="text-sm text-gray-700">• {a}</li>
                ))}
              </ul>
            </div>

            {/* Final Assessment */}
            <div className="p-4 bg-blue-50 rounded-lg">
              <h3 className="font-medium text-blue-700 mb-1">Final Assessment</h3>
              <p className="text-sm text-gray-700">{feedback.finalAssessment}</p>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

