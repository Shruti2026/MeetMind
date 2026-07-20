import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getMeeting } from '../api/meetingApi'
import {
  saveTextTranscript,
  saveAudioTranscript,
  processMeeting,
  getSummary,
} from '../api/processingApi'

function MeetingDetail() {
  const { id } = useParams()
  const [meeting, setMeeting] = useState(null)
  const [error, setError] = useState('')

  const [transcriptText, setTranscriptText] = useState('')
  const [audioFile, setAudioFile] = useState(null)
  const [savingTranscript, setSavingTranscript] = useState(false)
  const [processing, setProcessing] = useState(false)
  const [summary, setSummary] = useState(null)
  const [processError, setProcessError] = useState('')

  const loadMeeting = () => {
    getMeeting(id)
      .then(({ data }) => setMeeting(data))
      .catch(() => setError('Failed to load meeting'))
  }

  useEffect(() => {
    loadMeeting()
  }, [id])

  useEffect(() => {
    if (meeting?.status === 'PROCESSED') {
      getSummary(id)
        .then(({ data }) => setSummary(data))
        .catch(() => {})
    }
  }, [meeting?.status, id])

  const handleSaveText = async (e) => {
    e.preventDefault()
    setSavingTranscript(true)
    setProcessError('')
    try {
      await saveTextTranscript(id, transcriptText)
      loadMeeting()
    } catch (err) {
      setProcessError(err.response?.data?.message || 'Failed to save transcript')
    } finally {
      setSavingTranscript(false)
    }
  }

  const handleUploadAudio = async (e) => {
    e.preventDefault()
    if (!audioFile) return
    setSavingTranscript(true)
    setProcessError('')
    try {
      await saveAudioTranscript(id, audioFile)
      loadMeeting()
    } catch (err) {
      setProcessError(err.response?.data?.message || 'Failed to transcribe audio')
    } finally {
      setSavingTranscript(false)
    }
  }

  const handleProcess = async () => {
    setProcessing(true)
    setProcessError('')
    try {
      const { data } = await processMeeting(id)
      setSummary(data)
      loadMeeting()
    } catch (err) {
      setProcessError(err.response?.data?.message || 'Failed to process meeting')
      loadMeeting()
    } finally {
      setProcessing(false)
    }
  }

  if (error) return <p className="text-center mt-10 text-red-600">{error}</p>
  if (!meeting) return <p className="text-center mt-10 text-gray-500">Loading...</p>

  return (
    <div className="min-h-screen bg-gray-50 px-6 py-8">
      <div className="max-w-2xl mx-auto bg-white p-8 rounded-lg shadow">
        <Link to="/meetings" className="text-sm text-indigo-600 hover:underline">
          &larr; Back to meetings
        </Link>
        <h1 className="text-2xl font-bold text-gray-900 mt-2">{meeting.title}</h1>
        <p className="text-sm text-gray-500 mt-1">Status: {meeting.status}</p>

        {meeting.meetingDate && (
          <p className="text-sm text-gray-500 mt-1">
            Date: {new Date(meeting.meetingDate).toLocaleString()}
          </p>
        )}
        {meeting.participants && (
          <p className="text-sm text-gray-500 mt-1">Participants: {meeting.participants}</p>
        )}

        <div className="mt-6 border-t pt-6">
          <h2 className="text-lg font-semibold text-gray-900">Transcript &amp; AI Summary</h2>

          {meeting.status !== 'PROCESSED' && (
            <div className="mt-4 space-y-6">
              <form onSubmit={handleSaveText} className="space-y-2">
                <label className="block text-sm font-medium text-gray-700">
                  Paste meeting transcript
                </label>
                <textarea
                  rows={5}
                  value={transcriptText}
                  onChange={(e) => setTranscriptText(e.target.value)}
                  className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
                <button
                  type="submit"
                  disabled={savingTranscript || !transcriptText.trim()}
                  className="bg-gray-800 text-white px-4 py-2 rounded-md text-sm hover:bg-gray-900 disabled:opacity-50"
                >
                  {savingTranscript ? 'Saving...' : 'Save Transcript'}
                </button>
              </form>

              <form onSubmit={handleUploadAudio} className="space-y-2">
                <label className="block text-sm font-medium text-gray-700">
                  Or upload audio (.mp3, .wav, .m4a)
                </label>
                <input
                  type="file"
                  accept=".mp3,.wav,.m4a"
                  onChange={(e) => setAudioFile(e.target.files[0])}
                  className="text-sm"
                />
                <button
                  type="submit"
                  disabled={savingTranscript || !audioFile}
                  className="bg-gray-800 text-white px-4 py-2 rounded-md text-sm hover:bg-gray-900 disabled:opacity-50 block"
                >
                  {savingTranscript ? 'Transcribing...' : 'Upload & Transcribe'}
                </button>
              </form>

              <button
                onClick={handleProcess}
                disabled={processing}
                className="w-full bg-indigo-600 text-white py-2 rounded-md hover:bg-indigo-700 disabled:opacity-50"
              >
                {processing ? 'Processing Meeting...' : 'Process Meeting'}
              </button>

              {processError && <p className="text-sm text-red-600">{processError}</p>}
            </div>
          )}

          {summary && (
            <div className="mt-6 space-y-4">
              <div>
                <h3 className="font-semibold text-gray-900">Summary</h3>
                <p className="text-sm text-gray-700 mt-1">{summary.summaryText}</p>
              </div>
              <div>
                <h3 className="font-semibold text-gray-900">Key Decisions</h3>
                <p className="text-sm text-gray-700 mt-1">{summary.keyDecisions}</p>
              </div>
              <div>
                <h3 className="font-semibold text-gray-900">Action Items</h3>
                <ul className="mt-2 space-y-2">
                  {summary.actionItems.map((item) => (
                    <li key={item.id} className="text-sm bg-gray-50 rounded-md p-3">
                      <p className="text-gray-900">{item.description}</p>
                      <p className="text-gray-500 mt-1">
                        {item.assignee ? `Assigned to ${item.assignee}` : 'Unassigned'}
                        {item.dueDate ? ` · Due ${item.dueDate}` : ''}
                      </p>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default MeetingDetail
