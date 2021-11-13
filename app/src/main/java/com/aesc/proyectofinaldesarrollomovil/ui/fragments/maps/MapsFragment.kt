package com.aesc.proyectofinaldesarrollomovil.ui.fragments.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.LocationDao
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText


class MapsFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnMyLocationClickListener {

    companion object {
        const val LOCATION_REQUEST_CODE = 0
    }

    private lateinit var viewModel: MapsViewModel
    private lateinit var map: GoogleMap
    private lateinit var locationDao: LocationDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.maps_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MapsViewModel::class.java]
        createMapFragment()
    }

    private fun createMapFragment() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(gMap: GoogleMap) {
        map = gMap
        createMarker()
        map.setOnMyLocationClickListener(this)
        enableMyLocation()
    }

    private fun createMarker() {
        val favoritePlace = LatLng(14.6415565, -90.5138935)
        map.addMarker(MarkerOptions().position(favoritePlace).title("Hello World!"))
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(favoritePlace, 18f),
            4000,
            null
        )
    }

    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(context, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    context,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!::map.isInitialized) return
        if (!isPermissionsGranted()) {
            map.isMyLocationEnabled = false
            Toast.makeText(
                context,
                "Para activar la localización ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onMyLocationClick(currentLocation: Location) {
        showBottomSheetDialog(currentLocation)
    }

    private fun showBottomSheetDialog(currentLocation: Location) {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout)

        val name = bottomSheetDialog.findViewById<TextInputEditText>(R.id.tieNameOfLocation)
        val cancel = bottomSheetDialog.findViewById<MaterialButton>(R.id.btnCancel)
        val send = bottomSheetDialog.findViewById<MaterialButton>(R.id.btnSend)

        cancel!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        send!!.setOnClickListener {
            locationDao = LocationDao()

            currentLocation.let {
                val nameLocation = name!!.text.toString()
                locationDao.addLocation(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    nameLocation
                )
            }

            bottomSheetDialog.dismiss()

            Toast.makeText(
                context,
                "Informacion Almacenada",
                Toast.LENGTH_SHORT
            ).show()
        }
        bottomSheetDialog.show()
    }
}