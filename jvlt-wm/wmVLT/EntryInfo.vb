Public Class EntryInfo

    Private _batch As Integer
    Private _dateAdded As Date
    Private _entryId As String
    Private _lastQueried As Date
    Private _mistakes As Integer
    Private _queried As Integer

    Public Property batch() As Integer
        Get
            Return _batch
        End Get
        Set(ByVal value As Integer)
            _batch = value
        End Set
    End Property

    Public Property dateAdded() As Date
        Get
            Return _dateAdded
        End Get
        Set(ByVal value As Date)
            _dateAdded = value
        End Set
    End Property

    Public Property entryId() As String
        Get
            Return _entryId
        End Get
        Set(ByVal value As String)
            _entryId = value
        End Set
    End Property

    Public Property lastQueried() As Date
        Get
            Return _lastQueried
        End Get
        Set(ByVal value As Date)
            _lastQueried = value
        End Set
    End Property

    Public Property mistakes() As Integer
        Get
            Return mistakes
        End Get
        Set(ByVal value As Integer)
            _mistakes = value
        End Set
    End Property

    Public Property queried() As Integer
        Get
            Return _queried
        End Get
        Set(ByVal value As Integer)
            _queried = value
        End Set
    End Property

    Public Sub New(ByVal entryId As String, ByVal batch As Integer, ByVal dateAdded As Date, ByVal lastQueried As Date, ByVal mistakes As Integer, ByVal queried As Integer)
        Me.batch = batch
        Me.entryId = entryId
        Me.dateAdded = dateAdded
        Me.lastQueried = lastQueried
        Me.mistakes = mistakes
        Me.queried = queried

    End Sub

    Public Function toXML() As XElement
        Dim ret As XElement = <entry-info></entry-info>
        ret.Add(New XAttribute("batch", batch))
        ret.Add(New XAttribute("date-added", dateAdded))
        ret.Add(New XAttribute("entry-id", entryId))
        ret.Add(New XAttribute("last-queried", lastQueried))
        ret.Add(New XAttribute("mistakes", mistakes))
        ret.Add(New XAttribute("queried", queried))

        Return ret
    End Function



End Class
