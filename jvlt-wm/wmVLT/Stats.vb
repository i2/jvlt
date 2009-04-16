Public Class Stats

    Private _version As String
    Private _entryInfos As Hashtable

    Public Property version() As String
        Get
            Return _version
        End Get
        Set(ByVal value As String)
            _version = value
        End Set
    End Property

    Public Property enrtyInfos() As Hashtable
        Get
            Return _entryInfos
        End Get
        Set(ByVal value As Hashtable)
            _entryInfos = value
        End Set
    End Property

    Public Sub New(ByVal version As String)
        Me.version = version
        Me.enrtyInfos = New Hashtable
    End Sub

    Public Function toXML() As XElement
        Dim ret As XElement = <stats></stats>

        ret.Add(New XAttribute("version", version))

        For Each elem As EntryInfo In enrtyInfos.Values
            ret.Add(elem.toXML)
        Next

        Return ret
    End Function
End Class
